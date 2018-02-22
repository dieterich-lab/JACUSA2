package lib.data;

import lib.data.has.hasReadArrestCount;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoData
extends BaseCallData
implements hasReadArrestCount {

	private ReadArrestCount readInfoCount;
	
	public BaseCallReadInfoData(final BaseCallReadInfoData data) {
		super(data);

		this.readInfoCount = data.readInfoCount.copy();
	}
	
	public BaseCallReadInfoData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate, referenceBase);

		readInfoCount = new ReadArrestCount();
	}
		
	@Override
	public ReadArrestCount getReadArrestCount() {
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
		return readInfoCount.getReadStart() + readInfoCount.getReadInternal() + readInfoCount.getReadEnd();
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String sep = ", ";
		
		sb.append(super.toString());
		sb.append('\n');

		sb.append("Read info: (start, inner, end) (");
		sb.append(getReadArrestCount().getReadStart());
		sb.append(sep);
		sb.append(getReadArrestCount().getReadInternal());
		sb.append(sep);
		sb.append(getReadArrestCount().getReadEnd());
		sb.append(") (arrest, through) ; (");
		sb.append(getReadArrestCount().getReadArrest());
		sb.append(sep);
		sb.append(getReadArrestCount().getReadThrough());
		sb.append(")");

		return sb.toString();
	}
	
}
