package lib.data.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.util.Coordinate;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;

public class SAMRecordWrapper {

	public enum OVERLAP {NONE, LEFT, BOTH, RIGHT};
	
	private final SAMRecord record;
	private boolean printed;
	private boolean isValid;
	private boolean decoded;
	private boolean mutated;

	private OVERLAP orientation;
	private Coordinate activeWindowCoordinate;
	
	private List<CigarElementWrapper> introns;
	private List<CigarElementWrapper> insertions;
	private List<CigarElementWrapper> deletions;
	private List<CigarElementWrapper> INDELs;
	
	private byte[] baseCalls;
	private byte[] baseQualities;

	// TODO use alignmentBlock instead
	private int[] referencePosition2readPosition;
	
	public SAMRecordWrapper(
			final boolean isValid, 
			final Coordinate activeWindowCoordinate,
			final SAMRecord record) {
		this.isValid = isValid;
		this.activeWindowCoordinate = activeWindowCoordinate;
		
		this.record = record;
		
		printed 	= false;
		decoded 	= false;
		mutated 	= false;
		
		introns 	= new ArrayList<CigarElementWrapper>(3);
		insertions 	= new ArrayList<CigarElementWrapper>(3);
		deletions 	= new ArrayList<CigarElementWrapper>(3);
		INDELs 		= new ArrayList<CigarElementWrapper>(6);
		
		updateOrientation(activeWindowCoordinate);
	}
	
	public SAMRecord getSAMRecord() {
		return record;
	}
	
	public boolean isPrinted() {
		return printed;
	}
	
	public OVERLAP getOrientation() {
		return orientation;
	}
	
	public void updateOrientation(final Coordinate coordinate) {
		byte check = 0;
		if (record.getAlignmentStart() <= coordinate.getStart()) {
			check |= 1;
		}
		if (record.getAlignmentEnd() >= coordinate.getEnd()) {
			check |= 2;
		}

		orientation = OVERLAP.values()[check];
	}
	
	public void setPrinted() {
		printed = true;
	}
	
	public void setMutated() {
		decoded = false;
		mutated = true;
		
		// reset
		introns.clear();
		insertions.clear();
		deletions.clear();
		INDELs.clear();
		
		baseCalls = null;
		baseQualities = null;
		referencePosition2readPosition = null;
	}
	
	public boolean isValid() {
		return isValid;
	}

	public boolean isMutated() {
		return mutated;
	}
	
	public boolean hasINDEL() {
		for (final CigarElement e : record.getCigar().getCigarElements()) {
			if (e.getOperator().equals(CigarOperator.I) || 
					e.getOperator().equals(CigarOperator.D)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean hasIntron() {
		for (final CigarElement e : record.getCigar().getCigarElements()) {
			if (e.getOperator().equals(CigarOperator.N)) {
				return true;
			}
		}

		return false;
	}

	protected void processHardClipping(final Position position, final CigarElement cigarElement) {
		// System.err.println("Hard Clipping not handled yet!");
	}
	
	protected void processSoftClipping(final Position position, final CigarElement cigarElement) {
		// override if needed
	}

	protected void processPadding(final Position position, final CigarElement cigarElement) {
		// override if needed
	}

	public int[] getDistances(final int genomicPosition, final List<CigarElementWrapper> wrappers) {
		final int[] distances = new int[wrappers.size()];

		for (int i = 0; i < wrappers.size(); ++i) {
			final CigarElementWrapper wrapper = wrappers.get(i);
			final int wrapperGenomicPosition = wrapper.getPosition().getReferencePosition();
			if (genomicPosition > wrapperGenomicPosition) {
				final int cigarElementLength = wrapper.getCigarElement().getLength();
				final int wrapperGenomicEndPosition = wrapperGenomicPosition + cigarElementLength;
				if (genomicPosition > wrapperGenomicEndPosition) {
					distances[i] = genomicPosition - wrapperGenomicEndPosition;
				} else {
					distances[i] = 0;	
				}
			} else {
				distances[i] = wrapperGenomicPosition - genomicPosition;  
			}
		}

		Arrays.sort(distances);
		return distances;
	}

	public boolean overlapsWindowBorders() {
		return getOrientation() != OVERLAP.NONE;
	}
	
	public boolean isWithinRead(final int referencePosition) {
		return referencePosition >= record.getAlignmentStart() && 
				referencePosition <= record.getAlignmentEnd();
	}
	
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
	
	public boolean isValidBaseCall(final int genomicPosition, final byte minQual) {
		final int offset = genomicPosition - record.getAlignmentStart();
		return baseQualities[offset] > 0 && baseQualities[offset] >= minQual;
	}
	
	public void processRecord() {
		if (decoded) {
			return;
		}
		
		final int length = record.getAlignmentEnd() - record.getAlignmentStart() + 1;
		baseCalls = new byte[length];
		baseQualities = new byte[length];
		referencePosition2readPosition = new int[length];

		final Position position = new Position(0, record.getAlignmentStart());

		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch(cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				processInsertion(position, cigarElement);
				position.advance(cigarElement);
				break;

			/*
			 * handle alignment/sequence match and mismatch
			 */
			case M:
			case EQ:
			case X:
				processAlignmentMatch(position, cigarElement);
				position.advance(cigarElement);
				break;

			/*
			 * handle hard clipping 
			 */
			case H:
				processHardClipping(position, cigarElement);
				position.advance(cigarElement);
				break;

			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				processDeletion(position, cigarElement);
				position.advance(cigarElement);
				break;

			case N:
				processSkipped(position, cigarElement);
				position.advance(cigarElement);
				break;

			/*
			 * soft clipping
			 */
			case S:
				processSoftClipping(position, cigarElement);
				position.advance(cigarElement);
				break;

			/*
			 * silent deletion from padded sequence
			 */
			case P:
				processPadding(position, cigarElement);
				position.advance(cigarElement);
				break;

			default:
				throw new RuntimeException("Unsupported Cigar Operator: " + cigarElement.getOperator().toString());
			}
		}
	}

	protected void processAlignmentMatch(final Position position, final CigarElement cigarElement) {
		System.arraycopy(
				record.getReadBases(),
				position.getReadPosition(),
				baseCalls,
				position.getReferencePosition() - record.getAlignmentStart(),
				cigarElement.getLength());

		System.arraycopy(
				record.getBaseQualities(),
				position.getReadPosition(),
				baseQualities,
				position.getReferencePosition()  - record.getAlignmentStart(),
				cigarElement.getLength());
	}

	protected void processInsertion(final Position position, final CigarElement cigarElement) {
		insertions.add(new CigarElementWrapper(position.clone(), cigarElement));
		INDELs.add(new CigarElementWrapper(position.clone(), cigarElement));
	}
	
	protected void processDeletion(final Position position, final CigarElement cigarElement) {
		deletions.add(new CigarElementWrapper(position.clone(), cigarElement));
		INDELs.add(new CigarElementWrapper(position.clone(), cigarElement));
	}
	
	protected void processSkipped(final Position position, final CigarElement cigarElement) {
		introns.add(new CigarElementWrapper(position.clone(), cigarElement));
	}

	public List<CigarElementWrapper> getIntrons() {
		return introns;
	}
	
	public List<CigarElementWrapper> getInsertion() {
		return insertions;
	}
	
	public List<CigarElementWrapper> getDeletion() {
		return deletions;
	}

	public List<CigarElementWrapper> getINDELs() {
		return INDELs;
	}

	public String toString() {
		return record.toString();
	}
	
	public Coordinate getActiveWindowCoordinate() {
		return activeWindowCoordinate;
	}
	
	private class Position {
		
		private int readPosition;
		private int referencePosition;
		
		public Position(final int readPosition, final int referencePosition) {
			this.readPosition 		= readPosition;
			this.referencePosition 	= referencePosition;
		}

		public void advance(final CigarElement e) {
			if (e.getOperator().consumesReferenceBases() && e.getOperator().consumesReadBases()) {
				final int offset = referencePosition - record.getAlignmentStart();
				for (int i = 0; i < e.getLength(); ++i) {
					referencePosition2readPosition[offset + i] = readPosition + i;
				}

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
	
	private class CigarElementWrapper {
		
		private Position position;
		private CigarElement cigarElement;
		
		public CigarElementWrapper(final Position position, final CigarElement cigarElement) {
			this.position = position;
			this.cigarElement = cigarElement;
		}
		
		public Position getPosition() {
			return position;
		}
		
		public CigarElement getCigarElement() {
			return cigarElement;
		}

	}
	
}
