package lib.data.builder.recordwrapper;

import java.util.ArrayList;
import java.util.List;

import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;

public class SAMRecordWrapper {

	public enum OVERLAP {NONE, LEFT, BOTH, RIGHT};
	
	private final SAMRecord record;
	private boolean printed;
	private boolean isValid;

	private List<CigarElementWrapper> cigarElementWrappers;

	// specific
	private List<Integer> skipped;
	private List<Integer> insertions;
	private List<Integer> deletions;
	private List<Integer> INDELs;

	private byte[] reference;
	
	public SAMRecordWrapper(
			final boolean isValid, 
			final SAMRecord record) {
		this.isValid = isValid;
		this.record = record;
		printed 	= false;

		cigarElementWrappers = new ArrayList<CigarElementWrapper>(record.getCigarLength());
		skipped 	= new ArrayList<Integer>(2);
		insertions 	= new ArrayList<Integer>(2);
		deletions 	= new ArrayList<Integer>(2);
		INDELs 		= new ArrayList<Integer>(4);
	}
	
	public SAMRecord getSAMRecord() {
		return record;
	}
	
	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted() {
		printed = true;
	}
	
	public boolean isValid() {
		return isValid;
	}

	// referencePosition needs to be 1-based
	public boolean isWithinRead(final int referencePosition) {
		return referencePosition >= record.getAlignmentStart() && 
				referencePosition <= record.getAlignmentEnd();
	}

	public byte[] getReference() {
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
	
	public boolean isProcessed () {
		return cigarElementWrappers.size() > 0;
	}
	
	public void process() {
		final Position position = new Position(0, record.getAlignmentStart());

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

			cigarElementWrappers.add(new CigarElementWrapper(position.clone(), cigarElement));
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
	
	public class Position {
		
		private int readPosition;
		private int referencePosition;
		
		public Position(final int readPosition, final int referencePosition) {
			this.readPosition 		= readPosition;
			this.referencePosition 	= referencePosition;
		}

		public void advance(final CigarElement e) {
			if (e.getOperator().consumesReferenceBases() && e.getOperator().consumesReadBases()) {
				referencePosition += e.getLength();
				readPosition += e.getLength();
			} else {
				if (e.getOperator().consumesReferenceBases()) {
					referencePosition += e.getLength();
				}
				
				if (e.getOperator().consumesReadBases()) {
					readPosition += e.getLength();
				}
			}
		}
		
		public int getReadPosition() {
			return readPosition;
		}
		
		public int getReferencePosition() {
			return referencePosition;
		}

		public Position clone() {
			return new Position(readPosition, referencePosition);
		}

	}

	public class CigarElementWrapper {
		
		private Position position;
		private CigarElement cigarElement;
		
		public CigarElementWrapper(final Position position, final CigarElement cigarElement) {
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
			return Coordinate.makeRelativePosition(activeWindowCoordinate, referencePosition);
		}
		
		public int convertRead2WindowPosition(final int readPosition, final Coordinate activeWindowCoordinate) {
			if (position.getReadPosition() < readPosition || position.getReadPosition() > readPosition) {
				return -1;
			}

			final int offset = readPosition - position.getReadPosition();
			int tmpReferencePosition = position.getReferencePosition() + offset;
			return convertReference2WindowPosition(tmpReferencePosition, activeWindowCoordinate);
		}

		public Position getPosition() {
			return position;
		}
		
		public CigarElement getCigarElement() {
			return cigarElement;
		}

	}
	
}
