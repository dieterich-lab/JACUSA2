package test.utlis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordComparator;
import htsjdk.samtools.SAMRecordCoordinateComparator;
import htsjdk.samtools.SAMRecordQueryNameComparator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.TextCigarCodec;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import htsjdk.samtools.util.StringUtil;

public class SAMRecordCollectionBuilder {

	private final String contig;
	private final String refSeq;
	
	private String[] refSeqModified;
	private String cigarStr;
	
	private final SAMFileHeader header;
	
	private final Collection<SAMRecord> records;

	public SAMRecordCollectionBuilder(
			final String contig, final String refSeq, final SAMFileHeader header) {
		this(false, SAMFileHeader.SortOrder.unsorted, contig, refSeq, header);
	}

	public SAMRecordCollectionBuilder(final boolean sortForMe, final SAMFileHeader.SortOrder sortOrder,
			final String contig, final String refSeq,
			final SAMFileHeader header) {
		
		this.contig = contig;
		this.refSeq = refSeq;
		cigarStr = StringUtil.join(
				"", 
				Collections.nCopies(refSeq.length(), CigarOperator.M.toString()) );
		
		refSeqModified = new String[refSeq.length()];
		for (int i = 0; i < refSeq.length(); ++i) {
			refSeqModified[i] = Character.toString(refSeq.charAt(i));
		}
		
		this.header = header;
		
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
	}

	public SAMFileHeader getHeader() {
		return header;
	}
	
	public Collection<SAMRecord> getRecords() {
		return records;
	}
	
	private String getCigarStr(final int start, final int readLength) {
		int length = 1;
		char op = cigarStr.charAt(start - 1);
		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < readLength; ++i) {
			final char tmpOp = cigarStr.charAt(start - 1 + i);
			if (op != tmpOp) {
				sb.append(length);
				sb.append(op);
				op = tmpOp;
				length = 1;
			} else {
				length++;
			}
		}
		sb.append(length);
		sb.append(op);
		return sb.toString();
	}
	
	public SAMRecordCollectionBuilder addRecord(final SAMRecord record) {
		records.add(record);
		return this;
	}
	
	public SAMRecordCollectionBuilder addRecords(final boolean negativeStrand, final int readLength) {
		for (int start = 1; start <= refSeq.length() - readLength + 1; ++start) {
			addRecord(start, negativeStrand, readLength);
		}
		return this;
	}
	
	public SAMRecordCollectionBuilder addRecord(final int start, final boolean negativeStrand, final int readLength) {
		final CigarSAMRecordBuilder builder = new CigarSAMRecordBuilder(contig, start, negativeStrand, refSeq, header);
		final Cigar cigar = TextCigarCodec.decode(getCigarStr(start, readLength).toString());

		int refPos = start - 1;
		for (final CigarElement cigarElement : cigar.getCigarElements()) {
			final int length = cigarElement.getLength();
			final CigarOperator cigarOperator = cigarElement.getOperator();
			switch (cigarOperator) {
			case M:
				// create seq with matches and mismatches
				final String tmpRefSeqModified = StringUtil.join(
						"", 
						Arrays.asList(
								Arrays.copyOfRange(refSeqModified, refPos, refPos + length)) );
				// check if seq length does not change 
				if (tmpRefSeqModified.length() != length) {
					throw new IllegalStateException();
				}
				// find matches and mismatches
				int matched = 0;
				String mismatchedSeq = new String();
				for (int i = 0; i < length; ++i) {
					// mismatch
					if (refSeq.substring(refPos, refPos + length).charAt(i) != tmpRefSeqModified.charAt(i)) {
						if (matched > 0) {
							builder.addMatch(matched);
							matched = 0;
						} else {
							mismatchedSeq += tmpRefSeqModified.charAt(i);
						}
					} else { // match
						if (mismatchedSeq.length() > 0) {
							builder.addMismatch(mismatchedSeq);
							mismatchedSeq = new String();
						} else {
							matched++;
						}
					}
				}
				// pending matched region
				if (matched > 0) {
					builder.addMatch(matched);
					matched = 0;
				}
				// pending mismatch region
				if (mismatchedSeq.length() > 0) {
					builder.addMismatch(mismatchedSeq);
					mismatchedSeq = new String();
				}
				break;

			case I:
				String seq = refSeqModified[refPos];
				builder.addInsertion(seq);
				break;

			case D:
				builder.addDeletion(length);
				break;
				
			case N:
				builder.addSkipped(readLength);
				break;

			default:
				throw new IllegalArgumentException();
			}
			if (cigarElement.getOperator().consumesReferenceBases()) {
				refPos += cigarElement.getLength();
			}
		}
		return addRecord(builder.build());
	}
	
	public SAMRecordCollectionBuilder addMismatch(final int position, final String seq) {
		addCigarOperator(CigarOperator.M, position, seq.length());
		for (int i = 0; i < seq.length(); ++i) {
			refSeqModified[position - 1 + i] = Character.toString(seq.charAt(i));
		}
		return this;
	}
	
	public SAMRecordCollectionBuilder addIntron(final int position, final int length) {
		addCigarOperator(CigarOperator.N, position, length);
		return this;
	}
	
	public SAMRecordCollectionBuilder addInsertion(final int position, final String seq) {
		addCigarOperator(CigarOperator.I, position, seq.length());
		for (int i = 0; i < seq.length(); ++i) {
			refSeqModified[position - 1 + i] = seq.substring(i);
		}
		return this;
	}
	
	public SAMRecordCollectionBuilder addDeletion(final int position, final int length) {
		addCigarOperator(CigarOperator.D, position, length);
		return this;
	}
	
	private void addCigarOperator(final CigarOperator cigarOperator, final int position, final int length) {
		cigarStr += Collections.nCopies(length, cigarOperator.toString());
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(contig);
		sb.append(' ');
		sb.append(header.getSequenceIndex(contig));
		sb.append(" (");
		sb.append(contig.length());
		sb.append(')');
		sb.append('\n');
		sb.append(refSeq);
		sb.append('\n');
		sb.append(StringUtil.join(" ", refSeqModified));
		sb.append('\n');
		sb.append(cigarStr);
		sb.append('\n');
		sb.append("Reads: ");
		sb.append(records.size());
		sb.append('\n');
		return sb.toString();
	}

	public static SAMFileHeader createHeader(final SortOrder sortOrder, final Map<String, String> chr2seq) {
		final List<SAMSequenceRecord> sequences = new ArrayList<SAMSequenceRecord>();
        for (final String chrom : chr2seq.keySet()) {
        	final String seq = chr2seq.get(chrom);
        	final int seqLength = seq.length();
            sequences.add(new SAMSequenceRecord(chrom, seqLength));
        }
        final SAMFileHeader header = new SAMFileHeader();    
        header.setSequenceDictionary(new SAMSequenceDictionary(sequences));
        header.setSortOrder(sortOrder);
        return header;
	}
	
}
