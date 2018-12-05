package lib.data.builder.recordwrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.util.Base;

public class MDRecordReferenceProvider implements RecordReferenceProvider {

	// from htsjdk SequenceUtils
	static final Pattern MD_PATTERN = Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn])|(\\^[ACTGNactgn]+))");
	
	private final List<CombinedPosition> mismatchPositions;
	private final Map<Integer, Byte> refPos2base;

	private final Iterator<CigarElementWrapper> it;
	private CigarElementWrapper current;
	private int currentMatches;
	
	public MDRecordReferenceProvider(final SAMRecordWrapper recordWrapper) {
		final int n = 5;
		mismatchPositions = new ArrayList<CombinedPosition>(n);
		refPos2base = new HashMap<Integer, Byte>(n);

		it = recordWrapper.getCigarElementWrappers().iterator();
		current = it.next();
		currentMatches = 0;		
		
		process(recordWrapper);
	}
	
	@Override
	public Base getReferenceBase(int referencePosition) {
		if (! refPos2base.containsKey(referencePosition)) {
			return Base.N;
		}
		
		return Base.valueOf(refPos2base.get(referencePosition));
	}

	private void process(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final String md = record.getStringAttribute(SAMTag.MD.name());
        if (md == null) {
            throw new SAMException("Cannot create reference from SAMRecord with no MD tag, read: " + record.getReadName());
        }
        
        for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int readPos = block.getReadStart() - 1;
			final int refPos = block.getReferenceStart();
			final int length = block.getLength();
			for (int i = 0; i < length; ++i) {
				final byte base = record.getReadBases()[readPos + i];
				refPos2base.put(refPos + i, base);
			}
		}
        
		// correct refBase based on MD and add missing base calls from deletions
		final Matcher match = MD_PATTERN.matcher(md);
		
		while (match.find()) {
            String matchGroup;
            if ((matchGroup = match.group(1)) != null) {
                // It's a number , meaning a series of matches
               advance(Integer.parseInt(matchGroup));
            } else if ((matchGroup = match.group(2)) != null) {
                // It's a single nucleotide, meaning a mismatch
            	final byte base = (byte)matchGroup.charAt(0);
            	final CombinedPosition position = getCurrentPosition();
            	int refPos = position.getReferencePosition();
        		refPos2base.put(refPos, base);
        		mismatchPositions.add(position.copy());
        		advance(1);
            } else if ((matchGroup = match.group(3)) != null) {
                // It's a deletion, starting with a caret

            	final CombinedPosition position = getCurrentPosition();
            	int refPos = position.getReferencePosition();
                // i = 1 -> don't include caret
            	for (int i = 1; i < matchGroup.length(); ++i) {
            		final byte base = (byte)matchGroup.charAt(i);
            		refPos2base.put(refPos + i, base);
            	}
            	advance(matchGroup.length() - 1);
            }
        }
	}
	
	public List<CombinedPosition> getMismatchPositions() {
		return Collections.unmodifiableList(mismatchPositions);
	}
		
	private void advance(final int matches) {
		currentMatches += matches;
		while (currentMatches > getMatches() && it.hasNext()) {
			current = it.next();
		}
	}

	private CombinedPosition getCurrentPosition() {
		final int offset = currentMatches - current.getPosition().getMatches();
		return current.getPosition().copy()
				.advance(offset);
	}
		
	private int getMatches() {
		return current.getPosition().getMatches();
	}
	
}
