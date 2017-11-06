package jacusa.data;

import jacusa.util.Coordinate;

public class BaseQualReadInfoData
extends BaseQualData
implements hasReadInfoCount {

	private ReadInfoCount readInfoCount;
	
	public BaseQualReadInfoData() {
		super();
		
		readInfoCount = new ReadInfoCount();
	}

	public BaseQualReadInfoData(final BaseQualReadInfoData pileupData) {
		super(pileupData);
		
		this.readInfoCount = pileupData.readInfoCount.copy();
	}
	
	public BaseQualReadInfoData(final Coordinate coordinate, final char referenceBase, final LIBRARY_TYPE libraryType) {
		super(coordinate, referenceBase, libraryType);
		
		readInfoCount = new ReadInfoCount();
	}
		
	@Override
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}
	
	@Override
	public void add(AbstractData abstractData) {
		super.add(abstractData);

		BaseQualReadInfoData baseQualReadInfoData = (BaseQualReadInfoData) abstractData;
		readInfoCount.add(baseQualReadInfoData.readInfoCount);
	}
	
	@Override
	public BaseQualReadInfoData copy() {
		return new BaseQualReadInfoData(this);
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
