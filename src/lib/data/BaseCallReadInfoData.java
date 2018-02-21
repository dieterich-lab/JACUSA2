package lib.data;

import lib.data.has.hasReadInfoCount;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoData
extends BaseCallData
implements hasReadInfoCount {

	private ReadInfoCount readInfoCount;
	
	public BaseCallReadInfoData(final BaseCallReadInfoData data) {
		super(data);

		this.readInfoCount = data.readInfoCount.copy();
	}
	
	public BaseCallReadInfoData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate, referenceBase);

		readInfoCount = new ReadInfoCount();
	}
		
	@Override
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}
	
	@Override
	public void add(AbstractData abstractData) {
		super.add(abstractData);

		BaseCallReadInfoData baseQualReadInfoData = (BaseCallReadInfoData) abstractData;
		readInfoCount.add(baseQualReadInfoData.readInfoCount);
	}
	
	@Override
	public BaseCallReadInfoData copy() {
		return new BaseCallReadInfoData(this);
	}
	
	@Override
	public int getCoverage() {
		return readInfoCount.getStart() + readInfoCount.getInner() + readInfoCount.getEnd();
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String sep = ", ";
		
		sb.append(super.toString());
		sb.append('\n');

		sb.append("Read info: (start, inner, end) (");
		sb.append(getReadInfoCount().getStart());
		sb.append(sep);
		sb.append(getReadInfoCount().getInner());
		sb.append(sep);
		sb.append(getReadInfoCount().getEnd());
		sb.append(") (arrest, through) ; (");
		sb.append(getReadInfoCount().getArrest());
		sb.append(sep);
		sb.append(getReadInfoCount().getThrough());
		sb.append(")");

		return sb.toString();
	}
	
}
