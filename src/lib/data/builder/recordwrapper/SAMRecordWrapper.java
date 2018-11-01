package lib.data.builder.recordwrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.util.StringUtil;

public class SAMRecordWrapper {

	private final SAMRecord record;
	private List<CigarElementWrapper> cigarElementWrappers;

	// specific
	private final List<Integer> skipped;
	private final List<Integer> insertions;
	private final List<Integer> deletions;
	private final List<Integer> INDELs;

	// TODO private byte[] reference;
	
	private RecordReferenceProvider recordRefProvider;
	
	public SAMRecordWrapper(final SAMRecord record) {

		this.record = record;
		
		cigarElementWrappers = new ArrayList<CigarElementWrapper>(record.getCigarLength());
		skipped 	= new ArrayList<Integer>(2);
		insertions 	= new ArrayList<Integer>(2);
		deletions 	= new ArrayList<Integer>(2);
		INDELs 		= new ArrayList<Integer>(4);
	}

	public SAMRecord getSAMRecord() {
		return record;
	}

	// referencePosition needs to be 1-based
	public boolean isWithinRead(final int referencePosition) {
		return referencePosition >= record.getAlignmentStart() && 
				referencePosition <= record.getAlignmentEnd();
	}

	/* TODO remove
	public byte[] getReferenceBlocks() {
		if (reference != null) {
			return reference;
		}
		// no MD field :-(
		if (! record.hasAttribute(SAMTag.MD.name())) {
			reference = new byte[0];
			return reference; 
		}
		
		// potential missing number(s)
		final String MD = "0" + record.getStringAttribute(SAMTag.MD.name()).toUpperCase();
	
		// init container size with read length
		reference = new byte[record.getReadLength()];
		int destPos = 0;
		// hack
		// copy read sequence to reference container / CONCATENATE mapped segments ignore DELs
		// some base calls, e.g.: insertions will be ignored
		// reference is independent of read position 
		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int srcPos = block.getReadStart() - 1;
			final int length = block.getLength();
			System.arraycopy(
					record.getReadBases(), 
					srcPos, 
					reference, 
					destPos, 
					length);
			destPos += length;
		}

		int position = 0;
		boolean nextInteger = true;
		// change to reference base based on MD string
		// FIXME use pattern
		for (String e : MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))")) {
			if (nextInteger) { // match
				// use read sequence
				int matchLength = Integer.parseInt(e);
				position += matchLength;
				nextInteger = false;	
			} else if (e.charAt(0) == '^') {
				// ignore deletions from reference
				nextInteger = true;
			} else { // mismatch
				reference[position] = (byte)e.toCharArray()[0];
				position += 1;
				nextInteger = true;
			}
		}

		return reference;
	}
	*/

	public RecordReferenceProvider getRecordReferenceProvider() {
		if (recordRefProvider == null) {
			recordRefProvider = new MDRecordReferenceProvider(this);
		}
		return recordRefProvider;
	}
	
	public boolean isProcessed () {
		return cigarElementWrappers.size() > 0;
	}
	
	public void process() {
		final CombinedPosition position = new CombinedPosition(0, record.getAlignmentStart());

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
			cigarElementWrappers.add(new CigarElementWrapper(position.copy(), cigarElement));
			index = cigarElementWrappers.size();
			position.advance(cigarElement);
		}
	}

	public List<CigarElementWrapper> getCigarElementWrappers() {
		return cigarElementWrappers;
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
		return record.toString();
	}
	
	public int getUpstreamMatch(final int index) {
		if (index == 0) {
			return 0;
		}
		
		final CigarElementWrapper upstream = cigarElementWrappers.get(index - 1);
		if (! upstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return upstream.getCigarElement().getLength();
	}
	
	public int getDownstreamMatch(final int index) {
		if (index == cigarElementWrappers.size() - 1) {
			return 0;
		}
		
		final CigarElementWrapper downstream = cigarElementWrappers.get(index + 1);
		if (! downstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return downstream.getCigarElement().getLength();
	}
	
	public int getReferencePos(final int matches) {
		int tmp = 0;
		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			int refPos = block.getReferenceStart();
			for (int i = 0; i < block.getLength(); ++i) {
				if (tmp == matches) {
					return refPos + i;
				}
				tmp += 1;
			}
		}
		
		return -1;
	}
	
	public class CigarElementWrapper {
		
		private CombinedPosition position;
		private CigarElement cigarElement;
		
		public CigarElementWrapper(final CombinedPosition position, final CigarElement cigarElement) {
			this.position = position;
			this.cigarElement = cigarElement;
		}
		
		public int getReferenceBlockLength() {
			return cigarElement.getOperator().consumesReferenceBases() ?
					cigarElement.getLength() : 0;
		}
		
		public int getReadBlockLength() {
			return cigarElement.getOperator().consumesReadBases() ?
					cigarElement.getLength() : 0;
		}

		public int convertReference2WindowPosition(final int referencePosition, final Coordinate activeWindowCoordinate) {
			return CoordinateUtil.makeRelativePosition(activeWindowCoordinate, referencePosition);
		}
		
		public int convertRead2WindowPosition(final int readPosition, final Coordinate activeWindowCoordinate) {
			if (position.getReadPosition() < readPosition || position.getReadPosition() > readPosition) {
				return -1;
			}

			final int offset = readPosition - position.getReadPosition();
			int tmpReferencePosition = position.getReferencePosition() + offset;
			return convertReference2WindowPosition(tmpReferencePosition, activeWindowCoordinate);
		}

		public CombinedPosition getPosition() {
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
	
	static public String printInfo(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
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
		//for (final AlignmentPosition position : recordWrapper.getRecordReferenceProvider().getMismatchRefPositions()) {
		for (final int position : recordWrapper.getRecordReferenceProvider().getMismatchRefPositions()) {	
			mismtachPositions.add(position);
		}
		sb.append(StringUtil.join(",", mismtachPositions));
		sb.append('\n');
		
		sb.append("CIGAR=");
		sb.append(record.getCigarString());
		sb.append('\n');
		
		sb.append("CIGAR");
		sb.append('\n');
		sb.append(printCIGAR(recordWrapper));
		sb.append('\n');
		
		sb.append("BASES");
		sb.append('\n');
		sb.append(printBases(recordWrapper));
		sb.append('\n');

		sb.append("ALIGNMENT");
		sb.append('\n');
		sb.append(printAlignment(recordWrapper));
		sb.append('\n');
		
		return sb.toString();
	}
	
	static public String printCIGAR(final SAMRecordWrapper recordWrapper) {
		final StringBuilder sb = new StringBuilder();
		
		int totalLength = 0;
		for (final CigarElement e : recordWrapper.getSAMRecord().getCigar().getCigarElements()) {
			totalLength += e.getLength();
			final Collection<CigarOperator> ops = Collections.nCopies(e.getLength(), e.getOperator());
			sb.append(StringUtil.join("", ops));
		}
		sb.append('\n');
		
		sb.append(printScale(0, totalLength));
		
		return sb.toString();
	}
	
	static public String printAlignment(final SAMRecordWrapper recordWrapper) {
		final StringBuilder sb = new StringBuilder();
		final SAMRecord record = recordWrapper.getSAMRecord();
		final int AlignmentStart = record.getAlignmentStart();
		final int length = record.getAlignmentEnd() - AlignmentStart + 1;
		
		final char[] ref = new char[length];
		Arrays.fill(ref, ' ');
		// for (final AlignmentPosition position : recordWrapper.getRecordReferenceProvider().getMismatchRefPositions()) {
		for (final int refPos : recordWrapper.getRecordReferenceProvider().getMismatchRefPositions()) {
			int i = refPos - AlignmentStart;
			ref[i] = recordWrapper.getRecordReferenceProvider().getReferenceBase(refPos).getChar();
		}
		sb.append(new String(ref));
		sb.append('\n');
		
		for (int pos = record.getAlignmentStart(); pos <= record.getAlignmentEnd(); ++pos) {
			final Base base = recordWrapper.getRecordReferenceProvider().getReferenceBase(pos);
			sb.append(base.getChar());
		}
		sb.append('\n');
		
		for (int pos = record.getAlignmentStart(); pos <= record.getAlignmentEnd(); ++pos) {
			final int readPos = record.getReadPositionAtReferencePosition(pos) - 1;
			if (readPos >= 0) {
				sb.append((char)recordWrapper.getSAMRecord().getReadBases()[readPos]);
			} else {
				sb.append('-');
			}
		}
		sb.append('\n');
		
		sb.append(printScale(0, length));
		return sb.toString();
	}
	
	static public String printBases(final SAMRecordWrapper recordWrapper) {
		final StringBuilder sb = new StringBuilder();
		
		String s = StringUtil.bytesToString(recordWrapper.getSAMRecord().getReadBases());
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
