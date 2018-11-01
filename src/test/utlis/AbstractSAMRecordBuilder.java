package test.utlis;

import java.util.Arrays;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.util.SequenceUtil;

public abstract class AbstractSAMRecordBuilder {

	public final static int READ_GROUP_ID = 1;
	private static int READS = 0;
	
	private final String contig;
	private final int start;
	private final boolean negativeStrand;
	
	private final String refSeq;
	private final SAMFileHeader header;
	
	public AbstractSAMRecordBuilder(
			final String contig, final int start, final boolean negativeStrand, 
			final String refSeq, 
			final SAMFileHeader header) {
		
		this.contig = contig;
		this.start = start;
		this.negativeStrand = negativeStrand;

		this.refSeq = refSeq;
		this.header = header;
	}

	protected String getContig() {
		return contig;
	}

	protected int getStart() {
		return start;
	}
	
	protected String getRefSeq() {
		return refSeq;
	}
	
	protected SAMFileHeader getHeader() {
		return header;
	}

    public SAMRecord build() throws SAMException {
        final SAMRecord record = new SAMRecord(header);
        record.setReadName(getNextReadName(contig, start, negativeStrand));

        final int seqId = getSequenceIndex(contig);
        record.setReferenceIndex(seqId);
        record.setAlignmentStart(start);
        record.setReadNegativeStrandFlag(negativeStrand);

        // TODO check that cigar and read bases match
        record.setCigarString(getCigar().toString());
       	record.setReadBases(getReadBases());

       	record.setMappingQuality(255);
        record.setAttribute(SAMTag.RG.name(), READ_GROUP_ID);

        record.setAttribute(SAMTag.NM.name(), SequenceUtil.calculateSamNmTagFromCigar(record));

        final int length = record.getReadLength();
        final byte[] quals = new byte[length];
        Arrays.fill(quals, (byte) 40);
        record.setBaseQualities(quals);

        return record;
    }

    protected abstract Cigar getCigar();
    protected abstract byte[] getReadBases();
    protected abstract String getMD();
    
    private int getSequenceIndex(final String contig) {
		final int seqId = header.getSequenceIndex(contig);
		if (seqId < 0) {
			throw new IllegalStateException("Unknown contig: " + contig);
		}
		return seqId;
	}
    
    private String getNextReadName(final String contig, final int start, final boolean negativeStrand) {
		READS++;
    	final StringBuilder sb = new StringBuilder();
		sb.append(contig);
		sb.append(':');
		sb.append(start);
		sb.append(':');
		if (negativeStrand) {
			sb.append('-');
		} else {
			sb.append('+');
		}
		sb.append(':');
		sb.append(READS);
		return sb.toString();
	}

    public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(contig);
		sb.append(' ');
		sb.append(start);
		sb.append(' ');
		sb.append(getCigar() == null ? '*' : getCigar().toString());
		sb.append('\n');
		sb.append(refSeq);
		sb.append('\n');
		return sb.toString();
	}
    
}
