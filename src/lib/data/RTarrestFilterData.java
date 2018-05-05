package lib.data;

import lib.data.has.filter.HasRTarrestFilterData;

public class RTarrestFilterData implements HasRTarrestFilterData {

	private boolean isHomopolymer;

	// distance filter related
	private BaseCallCount INDEL_DistanceFilterData;
	private BaseCallCount spliceSiteDistanceFilterData;

	public RTarrestFilterData() {
		isHomopolymer = false;
	}

	public RTarrestFilterData(RTarrestFilterData rtArrestFilterData) {
		isHomopolymer 					= rtArrestFilterData.isHomopolymer;
		
		// distance filter related
		INDEL_DistanceFilterData		= rtArrestFilterData.INDEL_DistanceFilterData;
		spliceSiteDistanceFilterData	= rtArrestFilterData.spliceSiteDistanceFilterData;
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
	public BaseCallCount getINDEL_FilterData() {
		return INDEL_DistanceFilterData;
	}
	
	@Override
	public void setINDEL_DistanceFilterData(BaseCallCount baseCallCount) {
		INDEL_DistanceFilterData = baseCallCount;
	}
	
	@Override
	public BaseCallCount getSpliceSiteFilterData() {
		return spliceSiteDistanceFilterData;
	}
	
	@Override
	public void setSpliceSiteDistanceFilterData(BaseCallCount baseCallCount) {
		spliceSiteDistanceFilterData = baseCallCount;
	}
	
	public void add(final RTarrestFilterData rtArrestFilterData) {
		if (rtArrestFilterData.isHomopolymer) {
			isHomopolymer = true;
		}
		
		// distance filter related
		add(rtArrestFilterData.INDEL_DistanceFilterData, INDEL_DistanceFilterData);
		add(rtArrestFilterData.spliceSiteDistanceFilterData, spliceSiteDistanceFilterData);
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
	
	public RTarrestFilterData copy() {
		return new RTarrestFilterData(this);
	}

	
}
