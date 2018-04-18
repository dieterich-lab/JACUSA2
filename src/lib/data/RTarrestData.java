package lib.data;

import lib.data.has.HasRTarrestCount;
import lib.util.coordinate.Coordinate;

public class RTarrestData
extends PileupData
implements HasRTarrestCount {

	private final RTarrestCount rtArrestCount;
	
	public RTarrestData(final RTarrestData rtArrestData) {
		super(rtArrestData);

		rtArrestCount = rtArrestData.rtArrestCount.copy();
	}
	
	public RTarrestData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate, referenceBase);

		rtArrestCount = new RTarrestCount();
	}
		
	@Override
	public RTarrestCount getRTarrestCount() {
		return rtArrestCount;
	}
	
	@Override
	public void add(AbstractData abstractData) {
		super.add(abstractData);

		RTarrestData rtArrestData = (RTarrestData) abstractData;
		rtArrestCount.add(rtArrestData.rtArrestCount);
	}
	
	@Override
	public RTarrestData copy() {
		return new RTarrestData(this);
	}
	
	@Override
	public int getCoverage() {
		return rtArrestCount.getReadStart() + rtArrestCount.getReadInternal() + rtArrestCount.getReadEnd();
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String sep = ", ";
		
		sb.append(super.toString());
		sb.append('\n');

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
	
}
