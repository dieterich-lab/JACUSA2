package test.utlis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordComparator;
import htsjdk.samtools.SAMRecordCoordinateComparator;
import htsjdk.samtools.SAMRecordQueryNameComparator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.TextCigarCodec;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.samtools.util.StringUtil;
import lib.util.Base;

public class SAMRecordBuilder {

	public static final int READ_GROUP_ID = 1;
	public static final byte DEFAULT_BASQ = 40;
	
	private final SAMFileHeader header;
	private final Map<String, String> contig2refSeq;
	
	private final Collection<SAMRecord> records;

	private Random random;

	public SAMRecordBuilder() {
		this(true, SAMFileHeader.SortOrder.coordinate, ReferenceSequence.get());
	}
	
	public SAMRecordBuilder(
			final boolean sortForMe, final SAMFileHeader.SortOrder sortOrder,
			final Map<String, String> contig2refSeq) {

		header 				= createHeader(sortOrder, contig2refSeq);
		this.contig2refSeq 	= contig2refSeq;
		
        if (sortForMe) {
            final SAMRecordComparator comparator;
            if (sortOrder == SAMFileHeader.SortOrder.queryname) {
                comparator = new SAMRecordQueryNameComparator();
            } else {
                comparator = new SAMRecordCoordinateComparator();
            }
            records = new TreeSet<SAMRecord>(comparator);
        } else {
            records = new ArrayList<SAMRecord>();
        }
        
        random = new Random(1);
	}

	private Base getRandomBase() {
		final int i = random.nextInt(Base.values().length);
		return Base.values()[i];
	}
	
	private byte[] getReadBases(
			final String contig, final int refStart, final boolean negativeStrand,
			final String cigarStr, final String readSeq) {
		
		final Cigar cigar 		= TextCigarCodec.decode(cigarStr);
		final int readLength 	= cigar.getReadLength();
		
		if (! readSeq.isEmpty()) {
			if (readLength != readSeq.length()) {
				throw new IllegalStateException();
			}
			return StringUtil.stringToBytes(readSeq);
		}
		
		return StringUtil.stringToBytes(contig2refSeq.get(contig));
	}
	
	public SAMRecordBuilder withStrategy(final String contig, final SAMRecordBuilderStrategy strategy) {
		strategy.useStrategy(contig, this);
		return this;
	}
	
	public SAMRecordBuilder withSERead(
			final String contig, final int refStart, final boolean negativeStrand,
			final String cigarStr, final String readSeq) {
		
		final SAMRecord record = new SAMRecord(header);
        record.setReadName(getNextReadName(contig, refStart, negativeStrand));

        final int seqId = getSequenceIndex(contig);
        record.setReferenceIndex(seqId);
        record.setAlignmentStart(refStart);
        record.setReadNegativeStrandFlag(negativeStrand);

        record.setCigarString(cigarStr);
       	record.setReadBases(getReadBases(contig, refStart, negativeStrand, cigarStr, readSeq));

       	record.setMappingQuality(255);
        record.setAttribute(SAMTag.RG.name(), READ_GROUP_ID);
        
        SequenceUtil.calculateMdAndNmTags(record, StringUtil.stringToBytes(contig2refSeq.get(contig)), true, true);

        final int length 	= record.getReadLength();
        final byte[] quals 	= new byte[length];
        Arrays.fill(quals, DEFAULT_BASQ);
        record.setBaseQualities(quals);
		
        records.add(record);
		return this;
	}
	
	public Collection<SAMRecord> getRecords() {
		return records;
	}

	private int getSequenceIndex(final String contig) {
		final int seqId = header.getSequenceIndex(contig);
		if (seqId < 0) {
			throw new IllegalStateException("Unknown contig: " + contig);
		}
		return seqId;
	}
	
	private String getNextReadName(final String contig, final int start, final boolean negativeStrand) {
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
		sb.append(records.size() + 1);
		return sb.toString();
	}
	
	public static SAMFileHeader createHeader(
			final SortOrder sortOrder, final Map<String, String> contig2refSeq) {
		
		final List<SAMSequenceRecord> sequences = contig2refSeq.entrySet().stream()
			.map(e -> new SAMSequenceRecord(e.getKey(), e.getValue().length()))
			.collect(Collectors.toList());
		
        final SAMFileHeader header = new SAMFileHeader(new SAMSequenceDictionary(sequences));    
        header.setSortOrder(sortOrder);
        return header;
	}	

	public Map<String, String> getContig2refSeq() {
		return contig2refSeq;
	}

	public static SAMRecord createSERead(
			final String contig, final int refStart, 
			final String cigarStr) {
		
		return createSERead(contig, refStart, false, cigarStr, new String());
	}
	
	public static SAMRecord createSERead(
			final String contig, final int refStart, final boolean negativeStrand,  
			final String cigarStr) {
		
		return createSERead(contig, refStart, negativeStrand, cigarStr, new String());
	}
	
	public static SAMRecord createSERead(
			final String contig, final int refStart, final boolean negativeStrand, 
			final String cigarStr, final String refSeq) {

		final List<SAMRecord> records = new ArrayList<>(new SAMRecordBuilder()
				.withSERead(contig, refStart, negativeStrand, cigarStr, refSeq)
				.getRecords() );
		return records.get(0);
	}
	
}
