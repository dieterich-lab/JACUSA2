package lib.data;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.count.BaseCallCount;
import lib.data.count.LRTarrestCount;
import lib.data.count.RTarrestCount;
import lib.data.filter.RefPos2BaseCallCountFilteredData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;
import lib.util.coordinate.Coordinate;

public class LRTarrestData
extends AbstractData
implements HasCoverage, HasBaseCallCount, HasReferenceBase, HasLRTarrestCount, HasRefPos2BaseCallCountFilterData {

	private byte referenceBase;
	private final BaseCallCount baseCallCount;
	private final LRTarrestCount lrtArrestCount;
	
	private final AbstractFilteredData<RefPos2BaseCallCount> refPos2BaseCallCountFilterData;
	
	public LRTarrestData(final LRTarrestData data) {
		super(data);
		referenceBase 	= data.referenceBase;
		baseCallCount	= data.baseCallCount.copy();
		lrtArrestCount 	= data.lrtArrestCount.copy();
		refPos2BaseCallCountFilterData = data.refPos2BaseCallCountFilterData.copy();
	}
	
	public LRTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);
		this.referenceBase 					= referenceBase;
		baseCallCount 						= new ArrayBaseCallCount();
		lrtArrestCount 						= new LRTarrestCount();
		refPos2BaseCallCountFilterData		= new RefPos2BaseCallCountFilteredData();
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}
	
	@Override
	public LRTarrestCount getLRTarrestCount() {
		return lrtArrestCount;
	}
	
	@Override
	public RTarrestCount getRTarrestCount() {
		return lrtArrestCount.getRTarrestCount();
	}
	

	
	@Override
	public LRTarrestData copy() {
		return new LRTarrestData(this);
	}

	@Override
	public int getCoverage() {
		final RTarrestCount rtArrestCount = lrtArrestCount.getRTarrestCount(); 
		return rtArrestCount.getReadStart() + 
				rtArrestCount.getReadInternal() + 
				rtArrestCount.getReadEnd();
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
	public AbstractFilteredData<RefPos2BaseCallCount> getRefPos2BaseCallCountFilterData() {
		return refPos2BaseCallCountFilterData;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String sep = ", ";
		
		final RTarrestCount readArrestCount = lrtArrestCount.getRTarrestCount();
		
		sb.append("Read info: (start, inner, end) (");
		sb.append(readArrestCount.getReadStart());
		sb.append(sep);
		sb.append(readArrestCount.getReadInternal());
		sb.append(sep);
		sb.append(readArrestCount.getReadEnd());
		sb.append(") (arrest, through) ; (");
		sb.append(readArrestCount.getReadArrest());
		sb.append(sep);
		sb.append(readArrestCount.getReadThrough());
		sb.append(")");

		return sb.toString();
	}

	public void merge(final LRTarrestData src) {
		referenceBase = src.referenceBase;
		baseCallCount.add(src.baseCallCount);
		lrtArrestCount.add(src.lrtArrestCount);
	}

}
