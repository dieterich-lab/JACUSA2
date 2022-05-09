package lib.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import lib.record.ProcessedRecord.CigarDetail;
import lib.util.Base;
import lib.util.Util;

/**
 * TODO
 */
public class MDRecordRefProvider implements RecordRefProvider {

	// from htsjdk SequenceUtils
	static final Pattern MD_PATTERN = Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn])|(\\^[ACTGNactgn]+))");
	
	private final ProcessedRecord record;
	
	private final List<AlignedPosition> mismatchPoss;
	private final Map<Integer, Byte> refPos2base;

	private final Iterator<CigarDetail> cigarElementIt;
	private CigarDetail curCigarElement;
	private int curMatchedBases;
	
	public MDRecordRefProvider(final ProcessedRecord record) {
		this.record = record;
		
		final int n 		= 5;
		mismatchPoss 	= new ArrayList<>(n);
		refPos2base 		= new HashMap<>(Util.noRehashCapacity(n));

		cigarElementIt 	= record.getCigarDetail().iterator();
		curCigarElement	= cigarElementIt.next();
		curMatchedBases = 0;		
		process(record);
	}
	
	@Override
	public Base getRefBase(int refPos, int readPos) {
		if (refPos2base.containsKey(refPos)) {
			return Base.valueOf(refPos2base.get(refPos));
		}
		return Base.valueOf(record.getSAMRecord().getReadBases()[readPos]);
	}

	private void process(final ProcessedRecord record) {
		final SAMRecord samRecord = record.getSAMRecord();
		final String md = samRecord.getStringAttribute(SAMTag.MD.name());
        if (md == null) {
            throw new SAMException("Cannot create reference from SAMRecord with no MD tag, read: " + samRecord.getReadName());
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
            	int refPos = position.getRefPos();
            	refPos2base.put(refPos, base);
        		mismatchPoss.add(position.copy());
        		advance(1);
            } else if ((matchGroup = match.group(3)) != null) {
                // It's a deletion, starting with a caret

            	final AlignedPosition position = getCurrentPosition();
            	int refPos = position.getRefPos();
            	
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
		return Collections.unmodifiableList(mismatchPoss);
	}
		
	private void advance(final int matches) {
		curMatchedBases += matches;
		
		while (cigarElementIt.hasNext()) {
			final int matchedBases = getMatchedBases();
			if (matchedBases <= curMatchedBases && curMatchedBases < matchedBases + curCigarElement.getNonSkippedMatches()) {
				if (curCigarElement.getCigarElement().getOperator() == CigarOperator.N) {
					curCigarElement = cigarElementIt.next();
				}
				return;
			} else {
				curCigarElement = cigarElementIt.next();
			}
		}
	}
	
	private AlignedPosition getCurrentPosition() {
		final int offset = curMatchedBases - getMatchedBases();
		return curCigarElement.getPosition().copy().advance(offset);
	}
	
	private int getMatchedBases() {
		return curCigarElement.getPosition().getNonSkippedMatches();
	}
	
}
