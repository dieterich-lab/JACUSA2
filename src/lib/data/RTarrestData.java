package lib.data;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.count.BaseCallCount;
import lib.data.count.RTarrestCount;
import lib.data.filter.BaseCallFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.data.has.filter.HasBooleanFilterData;
import lib.util.coordinate.Coordinate;

public class RTarrestData
extends AbstractData
implements HasRTcount, HasBaseCallCount, HasArrestBaseCallCount, HasThroughBaseCallCount, HasReferenceBase, HasBooleanFilterData, HasBaseCallCountFilterData {

	private byte referenceBase;
	
	private final BaseCallCount arrestBaseCallCount;
	private final BaseCallCount throughBaseCallCount;
	private final BaseCallCount baseCallCount;
	
	private final RTarrestCount rtArrestCount;
	
	private final AbstractFilteredData<BaseCallCount> baseCallCountFilterData;
	private final AbstractFilteredData<Boolean> booleanFilterData;
	
	public RTarrestData(final RTarrestData src) {
		super(src);

		referenceBase = src.referenceBase;
		
		arrestBaseCallCount = src.arrestBaseCallCount.copy();
		throughBaseCallCount = src.throughBaseCallCount.copy();
		baseCallCount = src.baseCallCount.copy();
		
		rtArrestCount = src.rtArrestCount.copy();
		
		baseCallCountFilterData = src.baseCallCountFilterData.copy();
		booleanFilterData		= src.booleanFilterData.copy();
	}
	
	public RTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, byte referenceBase) {
		super(libraryType, coordinate);
		
		referenceBase = 'N';
		
		arrestBaseCallCount 	= new ArrayBaseCallCount();
		throughBaseCallCount 	= new ArrayBaseCallCount();
		baseCallCount 			= new ArrayBaseCallCount();
		
		rtArrestCount 			= new RTarrestCount();
		
		baseCallCountFilterData	= new BaseCallFilteredData();
		booleanFilterData		= new BooleanFilteredData();
	}
		
	@Override
	public RTarrestCount getRTarrestCount() {
		return rtArrestCount;
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

	@Override
	public AbstractFilteredData<BaseCallCount> getBaseCallCountFilterData() {
		return baseCallCountFilterData;
	}
	
	@Override
	public AbstractFilteredData<Boolean> getBooleanFilterData() {
		return booleanFilterData;
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

	public void merge(final RTarrestData src) {
		referenceBase = src.referenceBase;
		
		arrestBaseCallCount.add(src.arrestBaseCallCount);
		throughBaseCallCount.add(src.throughBaseCallCount);
		baseCallCount.add(src.baseCallCount);
		
		rtArrestCount.add(src.rtArrestCount);

		baseCallCountFilterData.merge(src.baseCallCountFilterData);
		booleanFilterData.merge(src.booleanFilterData);
	}
	
}
