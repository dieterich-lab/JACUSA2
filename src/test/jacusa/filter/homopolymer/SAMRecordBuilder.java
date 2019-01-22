package test.jacusa.filter.homopolymer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
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
import lib.data.builder.recordwrapper.CombinedPosition;
import lib.util.Base;
import test.utlis.MDtraverse;

public class SAMRecordBuilder {

	public static final int READ_GROUP_ID = 1;
	public static final byte DEFAULT_BASQ = 40;
	
	private final SAMFileHeader header;
	private final Map<String, String> contig2refSeq;
	
	private final Collection<SAMRecord> records;

	private Random random;
	
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
			final String contig, final int referenceStartPosition, final boolean negativeStrand,
			final String cigarStr, final String MD) {
		
		final Cigar cigar 		= TextCigarCodec.decode(cigarStr);
		final int readLength 	= cigar.getReadLength();
		final byte[] readBases 	= new byte[readLength];

		final MDtraverse mdTraverse = new MDtraverse(MD);
		int matchedBases 			= 0;
		
		final String refSeq 					= contig2refSeq.get(contig);
		final CombinedPosition combinedPosition = new CombinedPosition(referenceStartPosition);
		
		for (final CigarElement cigarElement : cigar.getCigarElements()) {
			final int cigarLength = cigarElement.getLength();
			switch (cigarElement.getOperator()) {
			
			case M:
				for (int i = 0; i < cigarLength; ++i) {
					final int readPosition 		= combinedPosition.getReadPosition() + i;
					final int referencePosition = combinedPosition.getReferencePosition() + i;
					++matchedBases;		
					
					if (mdTraverse.containsBases(matchedBases)) {
						final String mdSeq = mdTraverse.getBases(matchedBases);
						for (int j = 0; j < mdSeq.length(); ++j) {
							final char base 		= mdSeq.charAt(j);
							readBases[readPosition] = (byte)base;
						}
					} else {
						final char refBase 		= refSeq.charAt(referencePosition);
						readBases[readPosition] = (byte)refBase;
					}
					
					
					
				}
				break;
			
			case I:
				// TODO insertion bases
				break;
				
			case D:
				// nothing to do
				break;
				
			case N:
				// nothing to do
				break;
				
			case S:
				for (int i = 0; i < cigarLength; ++i) {
					final int readPosition 	= combinedPosition.getReadPosition() + i;
					final Base randomBase 	= getRandomBase();
					readBases[readPosition] = randomBase.getByte();
				}
				break;
				
			case H:
				// nothing to do
				break;
			
			default:
				throw new IllegalStateException("Unsupported CigarOperator: " + cigarElement.getOperator());
			}
			
			combinedPosition.advance(cigarElement);
		}
		
		return readBases;
	}
	
	public SAMRecordBuilder with(
			final String contig, final int refPos, final boolean negativeStrand,
			final String cigarStr, final String MD) {
		
		final SAMRecord record = new SAMRecord(header);
        record.setReadName(getNextReadName(contig, refPos, negativeStrand));

        final int seqId = getSequenceIndex(contig);
        record.setReferenceIndex(seqId);
        record.setAlignmentStart(refPos);
        record.setReadNegativeStrandFlag(negativeStrand);

        // TODO check that cigar and read bases match
        record.setCigarString(cigarStr);
       	record.setReadBases(getReadBases(contig, refPos, negativeStrand, cigarStr, MD));

       	record.setMappingQuality(255);
        record.setAttribute(SAMTag.RG.name(), READ_GROUP_ID);
        record.setAttribute(SAMTag.NM.name(), SequenceUtil.calculateSamNmTagFromCigar(record));

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
	
}
