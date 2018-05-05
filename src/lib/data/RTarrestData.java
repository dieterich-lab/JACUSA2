package lib.data;

import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.has.filter.HasRTarrestFilterData;
import lib.util.coordinate.Coordinate;

public class RTarrestData
extends AbstractData
implements HasRTarrestCount, HasBaseCallCount, HasArrestBaseCallCount, HasThroughBaseCallCount, HasReferenceBase, HasRTarrestFilterData {

	private byte referenceBase;
	
	private final BaseCallCount arrestBaseCallCount;
	private final BaseCallCount throughBaseCallCount;
	private final BaseCallCount baseCallCount;
	
	private final RTarrestCount rtArrestCount;
	
	private final RTarrestFilterData rtArrestFilterData;
	
	public RTarrestData(final RTarrestData rtArrestData) {
		super(rtArrestData);

		referenceBase = rtArrestData.referenceBase;
		
		arrestBaseCallCount = rtArrestData.arrestBaseCallCount.copy();
		throughBaseCallCount = rtArrestData.throughBaseCallCount.copy();
		baseCallCount = rtArrestData.baseCallCount.copy();
		
		rtArrestCount = rtArrestData.rtArrestCount.copy();
		
		rtArrestFilterData = rtArrestData.rtArrestFilterData.copy();
	}
	
	public RTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, byte referenceBase) {
		super(libraryType, coordinate);
		
		referenceBase = 'N';
		
		arrestBaseCallCount = new BaseCallCount();
		throughBaseCallCount = new BaseCallCount();
		baseCallCount = new BaseCallCount();
		
		rtArrestCount = new RTarrestCount();
		
		rtArrestFilterData = new RTarrestFilterData();
	}
		
	@Override
	public RTarrestCount getRTarrestCount() {
		return rtArrestCount;
	}
	
	@Override
	public void add(final AbstractData abstractData) {
		final RTarrestData rtArrestData = (RTarrestData) abstractData;
		
		referenceBase = rtArrestData.referenceBase;
		
		arrestBaseCallCount.add(rtArrestData.arrestBaseCallCount);
		throughBaseCallCount.add(rtArrestData.throughBaseCallCount);
		baseCallCount.add(rtArrestData.baseCallCount);
		
		rtArrestCount.add(rtArrestData.rtArrestCount);
		
		rtArrestFilterData.add(rtArrestData.rtArrestFilterData);
	}
	
	@Override
	public RTarrestData copy() {
		return new RTarrestData(this);
	}
	
	@Override
	public int getCoverage() {
		return rtArrestCount.getReadStart() + rtArrestCount.getReadInternal() + rtArrestCount.getReadEnd();
	}
	

	@Override
	public byte getReferenceBase() {
		return referenceBase;
	}

	@Override
	public void setReferenceBase(byte referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public BaseCallCount getThroughBaseCallCount() {
		return throughBaseCallCount;
	}

	@Override
	public BaseCallCount getArrestBaseCallCount() {
		return arrestBaseCallCount;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String sep = ", ";

		sb.append("Read info: (start, inner, end) (");
		sb.append(getRTarrestCount().getReadStart());
		sb.append(sep);
		sb.append(getRTarrestCount().getReadInternal());
		sb.append(sep);
		sb.append(getRTarrestCount().getReadEnd());
		sb.append(") (arrest, through) ; (");
		sb.append(getRTarrestCount().getReadArrest());
		sb.append(sep);
		sb.append(getRTarrestCount().getReadThrough());
		sb.append(")");

		return sb.toString();
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}
	
	@Override
	public boolean isHomopolymer() {
		return rtArrestFilterData.isHomopolymer();
	}

	@Override
	public void setHomopolymer(boolean b) {
		rtArrestFilterData.setHomopolymer(b);
	}

	@Override
	public BaseCallCount getINDEL_FilterData() {
		return rtArrestFilterData.getINDEL_FilterData();
	}

	@Override
	public void setINDEL_DistanceFilterData(BaseCallCount baseCallCount) {
		rtArrestFilterData.setINDEL_DistanceFilterData(baseCallCount);
	}

	@Override
	public BaseCallCount getSpliceSiteFilterData() {
		return rtArrestFilterData.getSpliceSiteFilterData();
	}

	@Override
	public void setSpliceSiteDistanceFilterData(BaseCallCount baseCallCount) {
		rtArrestFilterData.setSpliceSiteDistanceFilterData(baseCallCount);
	}

}
