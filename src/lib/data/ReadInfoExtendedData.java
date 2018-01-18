package lib.data;

import lib.data.has.hasCoverage;
import lib.data.has.hasReadInfoExtendedCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.Coordinate;

public class ReadInfoExtendedData
extends AbstractData
implements hasCoverage, hasReferenceBase, hasReadInfoExtendedCount {

	private byte referenceBase;
	private ReadInfoExtendedCount readInfoExtendedCount;
	
	public ReadInfoExtendedData(final ReadInfoExtendedData data) {
		super(data);

		this.referenceBase = data.referenceBase;
		this.readInfoExtendedCount = data.readInfoExtendedCount.copy();
	}
	
	public ReadInfoExtendedData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);

		this.referenceBase = referenceBase;
		readInfoExtendedCount = new ReadInfoExtendedCount();
	}
		
	@Override
	public ReadInfoExtendedCount getReadInfoExtendedCount() {
		return readInfoExtendedCount;
	}
	
	@Override
	public void add(AbstractData abstractData) {
		ReadInfoExtendedData baseQualReadInfoData = (ReadInfoExtendedData) abstractData;
		referenceBase = baseQualReadInfoData.referenceBase;
		readInfoExtendedCount.add(baseQualReadInfoData.readInfoExtendedCount);
	}
	
	@Override
	public ReadInfoExtendedData copy() {
		return new ReadInfoExtendedData(this);
	}

	@Override
	public int getCoverage() {
		return readInfoExtendedCount.getStart() + readInfoExtendedCount.getInner() + readInfoExtendedCount.getEnd();
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
		
		sb.append("Read info: (start, inner, end) (");
		sb.append(getReadInfoExtendedCount().getStart());
		sb.append(sep);
		sb.append(getReadInfoExtendedCount().getInner());
		sb.append(sep);
		sb.append(getReadInfoExtendedCount().getEnd());
		sb.append(") (arrest, through) ; (");
		sb.append(getReadInfoExtendedCount().getArrest());
		sb.append(sep);
		sb.append(getReadInfoExtendedCount().getThrough());
		sb.append(")");

		return sb.toString();
	}
	
}
