package lib.data;

import lib.data.has.HasPileupCount;
import lib.data.has.filter.PileupFilterData;
import lib.data.has.filter.HasPileupFilterData;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class PileupData
extends AbstractData
implements HasPileupCount, HasPileupFilterData {

	private final PileupCount pileupCount;
	private final STRAND effectiveStrand;

	private final PileupFilterData pileupFilterData;
	
	public PileupData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		super(libraryType, coordinate);

		pileupCount 		= new PileupCount();
		effectiveStrand		= STRAND.UNKNOWN;

		pileupFilterData	= new PileupFilterData();
	}
	
	public PileupData(final PileupData pileupData) {
		super(pileupData);

		this.pileupCount 		= pileupData.pileupCount.copy();
		this.effectiveStrand 	= pileupData.effectiveStrand;
		
		this.pileupFilterData	= pileupData.pileupFilterData.copy();
	}
	
	public PileupData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {

		super(libraryType, coordinate);

		pileupCount = new PileupCount();
		pileupCount.setReferenceBase(referenceBase);
		
		this.effectiveStrand	= STRAND.UNKNOWN;
		pileupFilterData		= new PileupFilterData();
	}
		
	@Override
	public PileupCount getPileupCount() {
		return pileupCount;
	}
	
	@Override
	public boolean isHomopolymer() {
		return pileupFilterData.isHomopolymer();
	}
	
	@Override
	public void setHomopolymer(boolean isHomopolymer) {
		pileupFilterData.setHomopolymer(isHomopolymer);
	}
	
	@Override
	public BaseCallCount getCombinedFilterData() {
		return pileupFilterData.getCombinedFilterData();
	}
	
	@Override
	public void setCombinedDistanceFilterData(final BaseCallCount baseCallCount) {
		pileupFilterData.setCombinedDistanceFilterData(baseCallCount);
	}

	@Override
	public BaseCallCount getINDEL_FilterData() {
		return pileupFilterData.getINDEL_FilterData();
	}
	
	@Override
	public BaseCallCount getReadPositionFilterData() {
		return pileupFilterData.getReadPositionFilterData();
	}
	
	@Override
	public BaseCallCount getSpliceSiteFilterData() {
		return pileupFilterData.getSpliceSiteFilterData();
	}
	
	@Override
	public void setINDEL_DistanceFilterData(final BaseCallCount baseCallCount) {
		pileupFilterData.setINDEL_DistanceFilterData(baseCallCount);
	}
	
	@Override
	public void setReadPositionDistanceFilterData(final BaseCallCount baseCallCount) {
		pileupFilterData.setReadPositionDistanceFilterData(baseCallCount);
	}
	
	@Override
	public void setSpliceSiteDistanceFilterData(final BaseCallCount baseCallCount) {
		pileupFilterData.setSpliceSiteDistanceFilterData(baseCallCount);
	}
	
	@Override
	public void add(AbstractData abstractData) {
		PileupData pileupData = (PileupData) abstractData;
		// TODO this is not nice - this should be done in PileupCount
		pileupCount.setReferenceBase(pileupData.getReferenceBase());
		// TODO check strand information
		pileupCount.add(pileupData.getPileupCount());

		// filter related
		pileupFilterData.add(pileupData.pileupFilterData);
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
