package lib.data;

import lib.data.basecall.BaseCallData;
import lib.data.has.hasReadInfoCount;
import lib.util.Coordinate;

public class BaseCallReadInfoData
extends BaseCallData
implements hasReadInfoCount {

	private ReadInfoCount readInfoCount;
	
	public BaseCallReadInfoData() {
		super();
		
		readInfoCount = new ReadInfoCount();
	}

	public BaseCallReadInfoData(final BaseCallReadInfoData data) {
		super(data);

		this.readInfoCount = data.readInfoCount.copy();
	}
	
	public BaseCallReadInfoData(final Coordinate coordinate, final LIBRARY_TYPE libraryType) {
		super(coordinate, libraryType);

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
