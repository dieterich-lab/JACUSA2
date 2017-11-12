package lib.data;

import lib.data.basecall.PileupData;
import lib.data.has.hasReadInfoCount;
import lib.util.Coordinate;

public class PileupReadInfoData
extends PileupData
implements hasReadInfoCount {

	private ReadInfoCount readInfoCount;
	
	public PileupReadInfoData() {
		super();
		
		readInfoCount = new ReadInfoCount();
	}

	public PileupReadInfoData(final PileupReadInfoData data) {
		super(data);

		this.readInfoCount = data.readInfoCount.copy();
	}
	
	public PileupReadInfoData(final Coordinate coordinate, final byte referenceBase, final LIBRARY_TYPE libraryType) {
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

		PileupReadInfoData baseQualReadInfoData = (PileupReadInfoData) abstractData;
		readInfoCount.add(baseQualReadInfoData.readInfoCount);
	}
	
	@Override
	public PileupReadInfoData copy() {
		return new PileupReadInfoData(this);
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
