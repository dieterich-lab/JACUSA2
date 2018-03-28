package lib.data;

import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;

public class LRTarrestData
extends AbstractData
implements HasCoverage, HasBaseCallCount, HasReferenceBase, HasLRTarrestCount {

	private byte referenceBase;
	private final BaseCallCount baseCallCount;
	private final LRTarrestCount lrtArrestCount;
	
	public LRTarrestData(final LRTarrestData data) {
		super(data);
		this.referenceBase 	= data.referenceBase;
		this.baseCallCount	= data.baseCallCount;
		this.lrtArrestCount = data.lrtArrestCount.copy();
	}
	
	public LRTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);

		this.referenceBase = referenceBase;
		baseCallCount = new BaseCallCount();
		lrtArrestCount = new LRTarrestCount();
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
	
}
