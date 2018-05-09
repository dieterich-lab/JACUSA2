package lib.data;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.count.BaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;

public class BaseCallData
extends AbstractData
implements HasBaseCallCount, HasReferenceBase {

	private byte referenceBase;
	private BaseCallCount baseCallCount;

	public BaseCallData(final BaseCallData pileupData) {
		super(pileupData);

		referenceBase = pileupData.referenceBase;
		this.baseCallCount = pileupData.baseCallCount.copy();
	}
	
	public BaseCallData(final LIBRARY_TYPE libraryType, final Coordinate coordinate, final byte referenceBase) {
		super(libraryType, coordinate);

		this.referenceBase = referenceBase;
		baseCallCount = new ArrayBaseCallCount();
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}

	@Override
	public BaseCallData copy() {
		return new BaseCallData(this);
	}

	@Override
	public byte getReferenceBase() {
		return referenceBase;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getBaseCallCount().toString());
		return sb.toString();
	}

	@Override
	public int getCoverage() {
		return getBaseCallCount().getCoverage();
	}

	@Override
	public void setReferenceBase(byte referenceBase) {
		this.referenceBase = referenceBase;
	}

	public void merge(final BaseCallData src) {
		referenceBase = src.referenceBase;
		baseCallCount.add(src.getBaseCallCount());
	}

}
