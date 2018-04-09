package lib.data.has.filter;

import lib.data.BaseCallCount;

public class PileupFilterData 
implements HasPileupFilterData {

	private boolean isHomopolymer;

	// distance filter related
	private BaseCallCount combinedDistanceFilterData;
	private BaseCallCount INDEL_DistanceFilterData;
	private BaseCallCount readPositionDistanceFilterData;
	private BaseCallCount spliceSiteDistanceFilterData;
	
	public PileupFilterData() {
		isHomopolymer = false;
	}

	public PileupFilterData(PileupFilterData pileupFilterData) {
		isHomopolymer 				= pileupFilterData.isHomopolymer;
		
		// distance filter related
		combinedDistanceFilterData		= pileupFilterData.combinedDistanceFilterData;
		INDEL_DistanceFilterData		= pileupFilterData.INDEL_DistanceFilterData;
		readPositionDistanceFilterData	= pileupFilterData.readPositionDistanceFilterData;
		spliceSiteDistanceFilterData	= pileupFilterData.spliceSiteDistanceFilterData;
		
	}
	
	@Override
	public boolean isHomopolymer() {
		return isHomopolymer;
	}
	
	@Override
	public void setHomopolymer(boolean isHomopolymer) {
		this.isHomopolymer = isHomopolymer;
	}
	
	@Override
	public BaseCallCount getCombinedFilterData() {
		return combinedDistanceFilterData;
	}
	
	@Override
	public void setCombinedDistanceFilterData(final BaseCallCount baseCallCount) {
		combinedDistanceFilterData = baseCallCount;
	}

	@Override
	public BaseCallCount getINDEL_FilterData() {
		return INDEL_DistanceFilterData;
	}
	
	@Override
	public void setINDEL_DistanceFilterData(BaseCallCount baseCallCount) {
		INDEL_DistanceFilterData = baseCallCount;
	}
	
	@Override
	public BaseCallCount getReadPositionFilterData() {
		return readPositionDistanceFilterData;
	}
	
	@Override
	public void setReadPositionDistanceFilterData(BaseCallCount baseCallCount) {
		readPositionDistanceFilterData = baseCallCount;
	}
	
	@Override
	public BaseCallCount getSpliceSiteFilterData() {
		return spliceSiteDistanceFilterData;
	}
	
	@Override
	public void setSpliceSiteDistanceFilterData(BaseCallCount baseCallCount) {
		spliceSiteDistanceFilterData = baseCallCount;
	}
	
	public void add(final PileupFilterData pileupFilterData) {
		if (pileupFilterData.isHomopolymer) {
			isHomopolymer = true;
		}
		
		// distance filter related
		add(pileupFilterData.combinedDistanceFilterData, combinedDistanceFilterData);
		add(pileupFilterData.INDEL_DistanceFilterData, INDEL_DistanceFilterData);
		add(pileupFilterData.readPositionDistanceFilterData, readPositionDistanceFilterData);
		add(pileupFilterData.spliceSiteDistanceFilterData, spliceSiteDistanceFilterData);
	}

	/**
	 * Helper function
	 * @param src
	 * @param dest
	 */
	private void add(BaseCallCount src, BaseCallCount dest) {
		if (src != null) {
			if(dest == null) {
				dest = new BaseCallCount();
			}
			dest.add(src);
		}
	}
	
	public PileupFilterData copy() {
		return new PileupFilterData(this);
	}

}