package test.utlis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.TextCigarCodec;
import htsjdk.samtools.util.StringUtil;

public class CigarSAMRecordBuilder extends AbstractSAMRecordBuilder {

	private static final Pattern MD_PATTERN = 
			Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn]+)|(\\^[ACTGNactgn]+))"); 
	
	private List<CigarElement> cigarElements;
	private String MD;
	private String readSeq;
	
	private int refPos;
	
	public CigarSAMRecordBuilder(
			final String contig, final int start, final boolean negativeStrand,
			final String refSeq,
			final SAMFileHeader header) {
		
		super(contig, start, negativeStrand, refSeq, header);
		cigarElements = new ArrayList<>();
		MD = new String();
		readSeq = new String();
		
		refPos = start - 1;
	}
	
	private void add(final CigarElement cigarElement, final String MD, final String seq) {
		final int size = cigarElements.size();
		int index = size - 1;
		if (cigarElement.getOperator() == CigarOperator.M &&
				! cigarElements.isEmpty() &&
				cigarElements.get(index).getOperator() == CigarOperator.M) {
			
			final int length = cigarElements.get(index).getLength() + cigarElement.getLength();
			final CigarElement newCigarElement = 
					new CigarElement(length, CigarOperator.M);
			cigarElements.set(index, newCigarElement);
		} else {
			cigarElements.add(cigarElement);
		}
		this.MD += MD;
		addSeq(seq);
		if (cigarElement.getOperator().consumesReferenceBases()) {
			refPos += cigarElement.getLength();
		}
	}
	
	public CigarSAMRecordBuilder addMatch(final int length) {
		add(
				new CigarElement(length, CigarOperator.M), 
				Integer.toString(length),
				getRefSeq().substring(
						refPos, 
						refPos + length) ); 
		return this;
	}
	
	public CigarSAMRecordBuilder addMismatch(final String seq) {
		add(
				new CigarElement(seq.length(), CigarOperator.M), 
				seq,
				seq);
		return this;
	}
	
	public CigarSAMRecordBuilder addInsertion(final String seq) {
		add(
				new CigarElement(seq.length(), CigarOperator.I), 
				new String(),
				seq);
		return this;
	}
	
	public CigarSAMRecordBuilder addDeletion(final int length) {
		add(
				new CigarElement(length, CigarOperator.D), 
				"^" + getRefSeq().substring(
						refPos, 
						refPos + length),
				new String() );
		return this;
	}
	
	public CigarSAMRecordBuilder addSkipped(final int length) {
		add(
				new CigarElement(length, CigarOperator.N), 
				new String(),
				new String() );
		return this;
	}
	
	public CigarSAMRecordBuilder addSoftClipping(final String seq) {
		add(
				new CigarElement(seq.length(), CigarOperator.S), 
				new String(),
				seq);
		return this;
	}

	public CigarSAMRecordBuilder addHardClipping(final int length) {
		add(
				new CigarElement(length, CigarOperator.H), 
				new String(),
				new String() );
		return this;
	}
	
	public CigarSAMRecordBuilder withCigarStr(final String cigarStr, final String MD, final String[] seq) {
		final Cigar cigar = TextCigarCodec.decode(cigarStr);
		final Matcher match = MD_PATTERN.matcher(MD);
		int seqIndex = 0;
		
		for (final CigarElement cigarElement : cigar.getCigarElements()) {
			final int cigarLength = cigarElement.getLength();
			switch (cigarElement.getOperator()) {
			
			case M:
				int totalMatchedBases = 0;
				while (totalMatchedBases < cigarLength && match.find()) {
					String matchGroup;
					if ((matchGroup = match.group(1)) != null) {
						// match(es)
                        final int matchedBases = Integer.parseInt(matchGroup);
                        totalMatchedBases += matchedBases;
                        addMatch(matchedBases);
                    } else if ((matchGroup = match.group(2)) != null) {
                    	// mismatch(es)
                    	totalMatchedBases += matchGroup.length();
                    	addMismatch(matchGroup);
                    } else if ((matchGroup = match.group(3)) != null) {
                    	// deletion
                    } else {
                        throw new IllegalStateException();
                    }
				}
				
				break;
			
			case I:
				addInsertion(seq[seqIndex++]);
				break;
				
			case D:
				addDeletion(cigarLength);
				break;
				
			case N:
				addSkipped(cigarLength);
				break;
				
			case S:
				addSoftClipping(seq[seqIndex++]);
				break;
				
			case H:
				addHardClipping(cigarLength);
				break;
			
			default:
				throw new IllegalStateException("Unsupported CigarOperator: " + cigarElement.getOperator());
			}
		}
		return this;
	}
	
	private void addSeq(final String seq) {
		readSeq += seq;
	}
		
	protected byte[] getReadBases() {
		return StringUtil.stringToBytes(readSeq);
	}

	@Override
	protected Cigar getCigar() {
		return new Cigar(cigarElements);
	}
	
	@Override
	protected String getMD() {
		return MD;
	}
	
}
