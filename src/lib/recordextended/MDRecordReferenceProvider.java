package lib.recordextended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import lib.util.Base;
import lib.util.Util;
import lib.recordextended.SAMRecordExtended.CigarElementExtended;

public class MDRecordReferenceProvider implements RecordReferenceProvider {

	// from htsjdk SequenceUtils
	static final Pattern MD_PATTERN = Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn])|(\\^[ACTGNactgn]+))");
	
	private final List<AlignedPosition> mismatchPositions;
	private final Map<Integer, Byte> refPos2base;

	private final Iterator<CigarElementExtended> cigarElementExtendedIterator;
	private CigarElementExtended currentCigarElementExtended;
	private int currentMatchedBases;
	
	public MDRecordReferenceProvider(final SAMRecordExtended recordExtended) {
		final int n 		= 5;
		mismatchPositions 	= new ArrayList<AlignedPosition>(n);
		refPos2base 		= new HashMap<Integer, Byte>(Util.noRehashCapacity(n));

		cigarElementExtendedIterator 	= recordExtended.getCigarElementExtended().iterator();
		currentCigarElementExtended	= cigarElementExtendedIterator.next();
		currentMatchedBases 			= 0;		
		process(recordExtended);
	}
	
	@Override
	public Base getReferenceBase(int refPos) {
		if (! refPos2base.containsKey(refPos)) {
			return Base.N;
		}
		
		return Base.valueOf(refPos2base.get(refPos));
	}

	private void process(final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final String md = record.getStringAttribute(SAMTag.MD.name());
        if (md == null) {
            throw new SAMException("Cannot create reference from SAMRecord with no MD tag, read: " + record.getReadName());
        }
        
        for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int readPos 	= block.getReadStart() - 1;
			final int refPos 	= block.getReferenceStart();
			final int length 	= block.getLength();
			
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
            	final AlignedPosition position = getCurrentPosition();
            	int refPos = position.getReferencePosition();
            	refPos2base.put(refPos, base);
        		mismatchPositions.add(position.copy());
        		advance(1);
            } else if ((matchGroup = match.group(3)) != null) {
                // It's a deletion, starting with a caret

            	final AlignedPosition position = getCurrentPosition();
            	int refPos = position.getReferencePosition();
            	
                // i = 1 -> don't include caret
            	for (int i = 1; i < matchGroup.length(); ++i) {
            		final byte base = (byte)matchGroup.charAt(i);
            		refPos2base.put(refPos + i - 1, base);
            	}
            	advance(matchGroup.length() - 1);
            }
        }
	}
	
	public List<AlignedPosition> getMismatchPositions() {
		return Collections.unmodifiableList(mismatchPositions);
	}
		
	private void advance(final int matches) {
		currentMatchedBases += matches;
		
		while (cigarElementExtendedIterator.hasNext()) {
			final int matchedBases = getMatchedBases();
			if (matchedBases <= currentMatchedBases && currentMatchedBases < matchedBases + currentCigarElementExtended.getNonSkippedMatches()) {
				if (currentCigarElementExtended.getCigarElement().getOperator() == CigarOperator.N) {
					currentCigarElementExtended = cigarElementExtendedIterator.next();
				}
				return;
			} else {
				currentCigarElementExtended = cigarElementExtendedIterator.next();
			}
		}
	}
	
	private AlignedPosition getCurrentPosition() {
		final int offset = currentMatchedBases - getMatchedBases();
		return currentCigarElementExtended.getPosition().copy().advance(offset);
	}
	
	private int getMatchedBases() {
		return currentCigarElementExtended.getPosition().getNonSkippedMatches();
	}
	
}
