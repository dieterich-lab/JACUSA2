package lib.data;

import java.util.HashMap;
import java.util.Map;

import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasLRTarrestFilterData;
import lib.util.coordinate.Coordinate;

public class LRTarrestData
extends AbstractData
implements HasCoverage, HasBaseCallCount, HasReferenceBase, HasLRTarrestCount, HasLRTarrestFilterData {

	private byte referenceBase;
	private final BaseCallCount baseCallCount;
	private final LRTarrestCount lrtArrestCount;

	private Map<Integer, BaseCallCount> lrtArrestINDEL_FilteredData;
	private Map<Integer, BaseCallCount> lrtArrestReadPositionFilteredData;
	private Map<Integer, BaseCallCount> lrtArrestCombinedFilteredData;
	private Map<Integer, BaseCallCount> lrtArrestSpliceSiteFilteredData;
	
	public LRTarrestData(final LRTarrestData data) {
		super(data);
		referenceBase 	= data.referenceBase;
		baseCallCount	= data.baseCallCount.copy();
		lrtArrestCount 	= data.lrtArrestCount.copy();
		lrtArrestINDEL_FilteredData 		= deepCopy(data.lrtArrestINDEL_FilteredData);
		lrtArrestReadPositionFilteredData 	= deepCopy(data.lrtArrestReadPositionFilteredData);
		lrtArrestCombinedFilteredData		= deepCopy(data.lrtArrestCombinedFilteredData);
		lrtArrestSpliceSiteFilteredData		= deepCopy(data.lrtArrestSpliceSiteFilteredData);
	}
	
	private Map<Integer, BaseCallCount> deepCopy(final Map<Integer, BaseCallCount> ref2bc) {

		final Map<Integer, BaseCallCount> tmp = new HashMap<Integer, BaseCallCount>(ref2bc.size());
		for (final int referencePosition : ref2bc.keySet()) {
			tmp.put(referencePosition, new BaseCallCount(ref2bc.get(referencePosition)));
		}

		return tmp;
	}
	
	public LRTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);
		this.referenceBase 					= referenceBase;
		baseCallCount 						= new BaseCallCount();
		lrtArrestCount 						= new LRTarrestCount();
		lrtArrestINDEL_FilteredData 		= new HashMap<Integer, BaseCallCount>();
		lrtArrestReadPositionFilteredData 	= new HashMap<Integer, BaseCallCount>();
		lrtArrestCombinedFilteredData		= new HashMap<Integer, BaseCallCount>();
		lrtArrestSpliceSiteFilteredData		= new HashMap<Integer, BaseCallCount>();
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
	public void add(AbstractData abstractData) {
		LRTarrestData lrtArrestData = (LRTarrestData) abstractData;
		referenceBase = lrtArrestData.referenceBase;
		baseCallCount.add(lrtArrestData.baseCallCount);
		lrtArrestCount.add(lrtArrestData.lrtArrestCount);
		add(lrtArrestCombinedFilteredData, lrtArrestData.lrtArrestCombinedFilteredData);
		add(lrtArrestSpliceSiteFilteredData, lrtArrestData.lrtArrestSpliceSiteFilteredData);
		add(lrtArrestINDEL_FilteredData, lrtArrestData.lrtArrestINDEL_FilteredData);
		add(lrtArrestReadPositionFilteredData, lrtArrestData.lrtArrestReadPositionFilteredData);
	}
	
	private void add(final Map<Integer, BaseCallCount> dest, final Map<Integer, BaseCallCount> src) {
		for (final int referencePosition : src.keySet()) {
			if (! dest.containsKey(referencePosition)) {
				dest.put(referencePosition, new BaseCallCount());
			}

			dest.get(referencePosition).add(src.get(referencePosition));
		}
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

	@Override
	public void setLRTarrestSpliceSiteFilteredData(
			Map<Integer, BaseCallCount> ref2bc) {
		lrtArrestSpliceSiteFilteredData = ref2bc;
	}

	@Override
	public Map<Integer, BaseCallCount> getLRTarrestSpliceSiteFilteredData() {
		return lrtArrestSpliceSiteFilteredData;
	}

	@Override
	public void setLRTarrestCombinedFilteredData(
			Map<Integer, BaseCallCount> ref2bc) {
		lrtArrestCombinedFilteredData = ref2bc;
	}

	@Override
	public Map<Integer, BaseCallCount> getLRTarrestCombinedFilteredData() {
		return lrtArrestCombinedFilteredData;
	}

	@Override
	public void setLRTarrestReadPositionFilteredData(
			Map<Integer, BaseCallCount> ref2bc) {
		lrtArrestReadPositionFilteredData = ref2bc;
	}

	@Override
	public Map<Integer, BaseCallCount> getLRTarrestReadPositionFilteredData() {
		return lrtArrestReadPositionFilteredData;
	}

	@Override
	public void setLRTarrestINDEL_FilteredData(
			Map<Integer, BaseCallCount> ref2bc) {
		lrtArrestINDEL_FilteredData = ref2bc;
	}

	@Override
	public Map<Integer, BaseCallCount> getLRTarrestINDEL_FilteredData() {
		return lrtArrestINDEL_FilteredData;
	}
	
}
