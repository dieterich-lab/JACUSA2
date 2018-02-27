package lib.data;

import lib.data.has.hasBaseCallCount;
import lib.data.has.hasCoverage;
import lib.data.has.hasLinkedReadArrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.Coordinate;

public class LinkedReadArrestCountData
extends AbstractData
implements hasCoverage, hasBaseCallCount, hasReferenceBase, hasLinkedReadArrestCount {

	private byte referenceBase;
	private BaseCallCount baseCallCount;
	private LinkedReadArrestCount linkedReadArrestCount;
	
	public LinkedReadArrestCountData(final LinkedReadArrestCountData data) {
		super(data);

		this.referenceBase 			= data.referenceBase;
		this.baseCallCount			= data.baseCallCount;
		this.linkedReadArrestCount 	= data.linkedReadArrestCount.copy();
	}
	
	public LinkedReadArrestCountData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);

		this.referenceBase = referenceBase;
		linkedReadArrestCount = new LinkedReadArrestCount();
	}
	
	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}
	
	@Override
	public LinkedReadArrestCount getLinkedReadArrestCount() {
		return linkedReadArrestCount;
	}
	
	@Override
	public ReadArrestCount getReadArrestCount() {
		return linkedReadArrestCount.getReadArrestCount();
	}
	
	@Override
	public void add(AbstractData abstractData) {
		LinkedReadArrestCountData baseQualReadInfoData = (LinkedReadArrestCountData) abstractData;
		referenceBase = baseQualReadInfoData.referenceBase;
		baseCallCount.add(baseQualReadInfoData.baseCallCount);
		linkedReadArrestCount.add(baseQualReadInfoData.linkedReadArrestCount);
	}
	
	@Override
	public LinkedReadArrestCountData copy() {
		return new LinkedReadArrestCountData(this);
	}

	@Override
	public int getCoverage() {
		final ReadArrestCount readArrestCount = linkedReadArrestCount.getReadArrestCount(); 
		return readArrestCount.getReadStart() + 
				readArrestCount.getReadInternal() + 
				readArrestCount.getReadEnd();
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
		
		final ReadArrestCount readArrestCount = linkedReadArrestCount.getReadArrestCount();
		
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
