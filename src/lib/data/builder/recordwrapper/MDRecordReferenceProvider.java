package lib.data.builder.recordwrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import lib.util.Base;

public class MDRecordReferenceProvider implements RecordReferenceProvider {

	// from htsjdk SequenceUtils
	static final Pattern MD_PATTERN = Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn])|(\\^[ACTGNactgn]+))");
	
	//private final List<AlignmentPosition> mismatchRefPos;
	private final List<Integer> mismatchRefPos;
	private final Map<Integer, Byte> refPos2base;
	
	public MDRecordReferenceProvider(final SAMRecordWrapper recordWrapper) {
		//mismatchRefPos = new ArrayList<AlignmentPosition>();
		mismatchRefPos = new ArrayList<Integer>();
		refPos2base = new HashMap<Integer, Byte>();
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
		// final AlignmentPosition position = new AlignmentPosition(record.getAlignmentBlocks());
		int matches = 0;
		while (match.find()) {
            String matchGroup;
            if ((matchGroup = match.group(1)) != null) {
                // It's a number , meaning a series of matches
               matches += Integer.parseInt(matchGroup);
            } else if ((matchGroup = match.group(2)) != null) {
                // It's a single nucleotide, meaning a mismatch
            	final byte base = (byte)matchGroup.charAt(0);
            	int refPos = recordWrapper.getReferencePos(matches);
        		refPos2base.put(refPos, base);
        		mismatchRefPos.add(refPos);
        		matches++;
            } else if ((matchGroup = match.group(3)) != null) {
            	/*
                // It's a deletion, starting with a caret
                // don't include caret
            	matchGroup = matchGroup.substring(1);
            	for (int i = 0; i < matchGroup.length(); ++i) {
            		final byte base = (byte)matchGroup.charAt(i);
            		final int refPos = position.getReferencePosition() + i;
            		refPos2base.put(refPos, base);
            	}
            	*/
            }
        }
	}

	
	public List<Integer> getMismatchRefPositions() {
		return Collections.unmodifiableList(mismatchRefPos);
	}
	
	/*
	@Override
	public List<AlignmentPosition> getMismatchRefPositions() {
		return Collections.unmodifiableList(mismatchRefPos);
	}
	*/
	
}
