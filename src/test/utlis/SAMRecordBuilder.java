package test.utlis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.QueryInterval;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileSource;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordComparator;
import htsjdk.samtools.SAMRecordCoordinateComparator;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMRecordQueryNameComparator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.SamPairUtil;
import htsjdk.samtools.SamReader;
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

	private static int readId = 1;
	
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

	public Base getRandomBase() {
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
		
		final byte[] readBases = new byte[readLength];
		final byte[] refBases = StringUtil.stringToBytes(contig2refSeq.get(contig));
		
		int tmpRefPos = refStart;
		int tmpReadPos 	= 0;
		for (final CigarElement cigarElement : cigar) {
			if (cigarElement.getOperator().consumesReadBases() && 
					cigarElement.getOperator().consumesReferenceBases()) {
				final int length = cigarElement.getLength();
				System.arraycopy(
						refBases, tmpRefPos - 1, 
						readBases, tmpReadPos, 
						length);
				tmpRefPos 	+= length;
				tmpReadPos 	+= length;
			}
		}
		
		return readBases;
	}
	
	public SAMRecordBuilder withStrategy(final String contig, final SAMRecordBuilderStrategy strategy) {
		strategy.useStrategy(contig, this);
		return this;
	}
	
	
	public SAMRecordBuilder withPERead(
			final String contig,
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final int refStart2, final boolean negativeStrand2, final String cigarStr2, final String readSeq2) {

		// simulate Single End Read
		final ToySAMRecord record = createSERecord(contig, refStart, negativeStrand, cigarStr, readSeq);
		final String readName = record.getReadName();
		final ToySAMRecord record2 = createSERecord(contig, refStart2, negativeStrand2, cigarStr2, readSeq2, readName);
		
		record.setReadPairedFlag(true);
		record.setProperPairFlag(true);
		record.setFirstOfPairFlag(true);
		record.setSecondOfPairFlag(false);
		
		record2.setReadPairedFlag(true);
		record2.setProperPairFlag(true);
		record2.setFirstOfPairFlag(false);
		record2.setSecondOfPairFlag(true);
		
		SamPairUtil.setMateInfo(record, record2, true);
		
		final ToySamReader reader = new ToySamReader();
		reader.addPair(record, record2);
		final SAMFileSource fileSource = new SAMFileSource(reader, null);
		record.setFileSource(fileSource);
		record2.setFileSource(fileSource);
		
		records.add(record);
		records.add(record2);
		return this;
	}
	
	public SAMRecordBuilder withSERead(
				final String contig, final int refStart, final boolean negativeStrand,
				final String cigarStr, final String readSeq) {
	
		records.add(createSERecord(contig, refStart, negativeStrand, cigarStr, readSeq));
		return this;
	}
	
	public ToySAMRecord createSERecord(
			final String contig, final int refStart, final boolean negativeStrand,
			final String cigarStr, final String readSeq) {
		
		return createSERecord(contig, refStart, negativeStrand, cigarStr, readSeq, getNextReadName());
	}
	
	private  ToySAMRecord createSERecord(
			final String contig, final int refStart, final boolean negativeStrand,
			final String cigarStr, final String readSeq, String readName) {
		
		final ToySAMRecord record = new ToySAMRecord(header);
        record.setReadName(readName);

        final int seqId = getSequenceIndex(contig);
        record.setReferenceIndex(seqId);
        record.setAlignmentStart(refStart);
        record.setReadNegativeStrandFlag(negativeStrand);

        record.setCigarString(cigarStr);
       	record.setReadBases(getReadBases(contig, refStart, negativeStrand, cigarStr, readSeq));

       	record.setMappingQuality(255);
        record.setAttribute(SAMTag.RG.name(), READ_GROUP_ID);
        
        final int length 	= record.getReadLength();
        final byte[] quals 	= new byte[length];
        Arrays.fill(quals, DEFAULT_BASQ);
        record.setBaseQualities(quals);

        record.validateCigar(-1);
        
        SequenceUtil.calculateMdAndNmTags(record, StringUtil.stringToBytes(contig2refSeq.get(contig)), true, true);
		return record;
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
	
	private String getNextReadName() {
    	return Integer.toString(readId++);
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
			final String contig, final int refStart,  
			final String cigarStr, final String readSeq) {
		
		return createSERead(contig, refStart, false, cigarStr, readSeq);
	}
	
	public static SAMRecord createSERead(
			final String contig, final int refStart, final boolean negativeStrand, 
			final String cigarStr, final String readSeq) {

		final List<SAMRecord> records = new ArrayList<>(new SAMRecordBuilder()
				.withSERead(contig, refStart, negativeStrand, cigarStr, readSeq)
				.getRecords() );
		return records.get(0);
	}
	private class ToySamReader implements SamReader {

		private Map<SAMRecord, SAMRecord> read2read;
		
		public ToySamReader() {
			read2read = new HashMap<SAMRecord, SAMRecord>();
		}
		
		public void addPair(SAMRecord read1, SAMRecord read2) {
			read2read.put(read1, read2);
			read2read.put(read2, read1);
		}
		
		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public SAMFileHeader getFileHeader() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Type type() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getResourceDescription() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasIndex() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Indexing indexing() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator query(String sequence, int start, int end, boolean contained) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryOverlapping(String sequence, int start, int end) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryContained(String sequence, int start, int end) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator query(QueryInterval[] intervals, boolean contained) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryOverlapping(QueryInterval[] intervals) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryContained(QueryInterval[] intervals) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryUnmapped() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecordIterator queryAlignmentStart(String sequence, int start) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SAMRecord queryMate(SAMRecord rec) {
			return read2read.get(rec);
			// TODO return read2read.entrySet().iterator().next().getValue();
		}
		
	}
	
	private class ToySAMRecord extends SAMRecord {
		
		private static final long serialVersionUID = 1L;

		public ToySAMRecord(final SAMFileHeader header) {
			super(header);
		}
		
		public void setFileSource(final SAMFileSource fileSource) {
			super.setFileSource(fileSource);
		}

		
	}
	
}
