package lib.data.builder.recordwrapper;

import java.util.ArrayList;
import java.util.List;

import lib.util.Coordinate;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

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
	
	/*
	public char getBaseCall(final int referencePosition) {
		final int offset = referencePosition - record.getAlignmentStart();
		return (char)baseCalls[offset];
	}
	
	public byte getBaseQuality(final int referencePosition) {
		final int offset = referencePosition - record.getAlignmentStart();
		return baseQualities[offset];
	}

	public int getReadPositionAtReferencePosition(final int referencePosition) {
		final int offset = referencePosition - record.getAlignmentStart();
		return referencePosition2readPosition[offset];
	}
	*/

	public boolean isProcessed () {
		return cigarElementWrappers.size() > 0;
	}
	
	public void process() {
		final Position position = new Position(0, record.getAlignmentStart());

		int index = 0;
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			cigarElementWrappers.add(new CigarElementWrapper(position.clone(), cigarElement));
			
			switch(cigarElement.getOperator()) {

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
			// TODO if outside window stop
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
			// TODO within block
			return Coordinate.makeRelativePosition(activeWindowCoordinate, referencePosition);
		}
		
		public int convertRead2WindowPosition(final int readPosition, final Coordinate activeWindowCoordinate) {
			// TODO within block
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
