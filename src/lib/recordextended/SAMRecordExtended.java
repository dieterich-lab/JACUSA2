package lib.recordextended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lib.util.Base;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.util.StringUtil;

public class SAMRecordExtended {

	private final SAMRecord record;
	private final List<CigarElementExtended> cigarElementExtended;

	private final List<Integer> skipped;
	private final List<Integer> insertions;
	private final List<Integer> deletions;
	private final List<Integer> INDELs;
	
	private RecordReferenceProvider recordRefProvider;

	public SAMRecordExtended(final SAMRecord record) {
		this.record = record;
		
		cigarElementExtended = new ArrayList<CigarElementExtended>(record.getCigarLength());
		skipped 	= new ArrayList<Integer>(2);
		insertions 	= new ArrayList<Integer>(2);
		deletions 	= new ArrayList<Integer>(2);
		INDELs 		= new ArrayList<Integer>(4);
		
		process();
	}

	public SAMRecord getSAMRecord() {
		return record;
	}

	public RecordReferenceProvider getRecordReferenceProvider() {
		if (recordRefProvider == null) {
			recordRefProvider = new MDRecordReferenceProvider(this);
		}
		return recordRefProvider;
	}
	
	private void process() {
		final AlignedPosition position = new AlignedPosition(record.getAlignmentStart());

		int index = 0;
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch (cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				insertions.add(index);
				INDELs.add(index);
				break;
			
			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				deletions.add(index);
				INDELs.add(index);
				break;
			
			/*
			 * handle deletion from the reference and introns
			 */
			case N:
				skipped.add(index);
				break;
				
			default:
				break;
			}
		
			cigarElementExtended.add(new CigarElementExtended(position.copy(), cigarElement));
			index = cigarElementExtended.size();
			position.advance(cigarElement);
		}
	}

	public List<CigarElementExtended> getCigarElementExtended() {
		return cigarElementExtended;
	}
	
	public List<Integer> getSkipped() {
		return skipped;
	}
	
	public List<Integer> getInsertion() {
		return insertions;
	}
	
	public List<Integer> getDeletion() {
		return deletions;
	}

	public List<Integer> getINDELs() {
		return INDELs;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.bytesToString(record.getReadBases()));
		return sb.toString();
	}
	
	public int getUpstreamMatch(final int index) {
		if (index == 0) {
			return 0;
		}
		
		final CigarElementExtended upstream = cigarElementExtended.get(index - 1);
		if (! upstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return upstream.getCigarElement().getLength();
	}
	
	public int getDownstreamMatch(final int index) {
		if (index == cigarElementExtended.size() - 1) {
			return 0;
		}
		
		final CigarElementExtended downstream = cigarElementExtended.get(index + 1);
		if (! downstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return downstream.getCigarElement().getLength();
	}
	
	public class CigarElementExtended {
		
		private AlignedPosition position;
		private CigarElement cigarElement;
		
		public CigarElementExtended(final AlignedPosition position, final CigarElement cigarElement) {
			this.position 		= position;
			this.cigarElement 	= cigarElement;
		}
		
		public int getReferenceBlockLength() {
			return cigarElement.getOperator().consumesReferenceBases() ?
					cigarElement.getLength() : 0;
		}
		
		public int getReadBlockLength() {
			return cigarElement.getOperator().consumesReadBases() ?
					cigarElement.getLength() : 0;
		}

		public AlignedPosition getPosition() {
			return position;
		}
		
		public CigarElement getCigarElement() {
			return cigarElement;
		}
	
	}

	@Override
	public int hashCode() {
		return record.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return record.equals(o);
	}
	
	static public String printInfo(final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final StringBuilder sb = new StringBuilder();
		
		final int nm = record.getIntegerAttribute(SAMTag.NM.name());
		sb.append("NM=");
		sb.append(nm);
		sb.append('\n');
		
		final String md = record.getStringAttribute(SAMTag.MD.name());
		sb.append("MD=");
		sb.append(md);
		sb.append('\n');
		
		sb.append("start=");
		sb.append(record.getAlignmentStart());
		sb.append('\n');
		
		sb.append("end=");
		sb.append(record.getAlignmentEnd());
		sb.append('\n');
		
		sb.append("mismatch=");
		final List<Integer> mismtachPositions = new ArrayList<>();
		for (final AlignedPosition position : recordExtended.getRecordReferenceProvider().getMismatchPositions()) {
			mismtachPositions.add(position.getReferencePosition());
		}

		sb.append(StringUtil.join(",", mismtachPositions));
		sb.append('\n');
		
		sb.append("CIGAR=");
		sb.append(record.getCigarString());
		sb.append('\n');
		
		sb.append("CIGAR");
		sb.append('\n');
		sb.append(printCIGAR(recordExtended));
		sb.append('\n');
		
		sb.append("BASES");
		sb.append('\n');
		sb.append(printBases(recordExtended));
		sb.append('\n');

		sb.append("ALIGNMENT");
		sb.append('\n');
		sb.append(printAlignment(recordExtended));
		sb.append('\n');
		
		return sb.toString();
	}
	
	static public String printCIGAR(final SAMRecordExtended recordExtended) {
		final StringBuilder sb = new StringBuilder();
		
		int totalLength = 0;
		for (final CigarElement e : recordExtended.getSAMRecord().getCigar().getCigarElements()) {
			totalLength += e.getLength();
			final Collection<CigarOperator> ops = Collections.nCopies(e.getLength(), e.getOperator());
			sb.append(StringUtil.join("", ops));
		}
		sb.append('\n');
		
		sb.append(printScale(0, totalLength));
		
		return sb.toString();
	}
	
	static public String printAlignment(final SAMRecordExtended recordExtended) {
		final StringBuilder sb = new StringBuilder();
		final SAMRecord record = recordExtended.getSAMRecord();
		final int AlignmentStart = record.getAlignmentStart();
		final int length = record.getAlignmentEnd() - AlignmentStart + 1;
		
		final char[] ref = new char[length];
		Arrays.fill(ref, ' ');
		for (final AlignedPosition position : recordExtended.getRecordReferenceProvider().getMismatchPositions()) {
			final int refPos = position.getReferencePosition();
			int i = refPos - AlignmentStart;
			ref[i] = recordExtended.getRecordReferenceProvider().getReferenceBase(refPos).getChar();
		}
		
		sb.append(new String(ref));
		sb.append('\n');
		
		for (int pos = record.getAlignmentStart(); pos <= record.getAlignmentEnd(); ++pos) {
			final Base base = recordExtended.getRecordReferenceProvider().getReferenceBase(pos);
			sb.append(base.getChar());
		}
		sb.append('\n');
		
		for (int pos = record.getAlignmentStart(); pos <= record.getAlignmentEnd(); ++pos) {
			final int readPos = record.getReadPositionAtReferencePosition(pos) - 1;
			if (readPos >= 0) {
				sb.append((char)recordExtended.getSAMRecord().getReadBases()[readPos]);
			} else {
				sb.append('-');
			}
		}
		sb.append('\n');
		
		sb.append(printScale(0, length));
		return sb.toString();
	}
	
	static public String printBases(final SAMRecordExtended recordExtended) {
		final StringBuilder sb = new StringBuilder();
		
		String s = StringUtil.bytesToString(recordExtended.getSAMRecord().getReadBases());
		sb.append(s);
		sb.append('\n');
		
		sb.append(printScale(0, s.length()));
		
		return sb.toString();
	}

	static public String printScale(final int prefix, final int length ) {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		
		for (int i = 0; i < prefix; ++i) {
			sb1.append(' ');
			sb2.append(' ');
		}

		for (int i = 0; i < length - prefix; ++i) {
			if (i % 10 == 0) {
				sb1.append(i % 10);
				sb2.append(i / 10);
			} else {
				sb1.append(i % 10);
				sb2.append(' ');
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(sb1.toString());
		sb.append('\n');
		sb.append(sb2.toString());
		return sb.toString();
	}
	
}
