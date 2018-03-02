package lib.data;

import lib.data.has.hasPileupCount;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class PileupData
extends AbstractData
implements hasPileupCount {

	private final PileupCount pileupCount;
	private final STRAND effectiveStrand;

	public PileupData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		super(libraryType, coordinate);

		pileupCount 	= new PileupCount();
		effectiveStrand	= STRAND.UNKNOWN;
	}
	
	public PileupData(final PileupData pileupData) {
		super(pileupData);
		this.pileupCount 		= pileupData.pileupCount.copy();
		this.effectiveStrand 	= pileupData.effectiveStrand;
	}
	
	public PileupData(final Coordinate coordinate, final byte referenceBase,
			final LIBRARY_TYPE libraryType) {

		super(libraryType, coordinate);

		pileupCount = new PileupCount();
		pileupCount.setReferenceBase(referenceBase);
		
		this.effectiveStrand= STRAND.UNKNOWN;
	}
		
	@Override
	public PileupCount getPileupCount() {
		return pileupCount;
	}

	@Override
	public void add(AbstractData abstractData) {
		PileupData pileupData = (PileupData) abstractData;
		// TODO check strand information
		pileupCount.add(pileupData.getPileupCount());
	}

	/**
	 * 
	 * @return
	 */
	public STRAND getEffectiveStrand() {
		return effectiveStrand;
	}
	
	@Override
	public PileupData copy() {
		return new PileupData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		if (getLibraryType() != null) {
			sb.append(getLibraryType().toString());
		} else {
			sb.append("null");
		}
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getPileupCount().toString());
		return sb.toString();
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return getPileupCount().getBaseCallCount();
	}

	@Override
	public int getCoverage() {
		return getPileupCount().getCoverage();
	}

	@Override
	public byte getReferenceBase() {
		return getPileupCount().getReferenceBase();
	}

	@Override
	public void setReferenceBase(final byte referenceBase) {
		getPileupCount().setReferenceBase(referenceBase);
	}

}
