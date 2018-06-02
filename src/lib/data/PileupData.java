package lib.data;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.basecall.map.MapBaseCallQualitityCount;
import lib.data.count.BaseCallCount;
import lib.data.count.PileupCount;
import lib.data.filter.BaseCallFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.has.HasPileupCount;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.data.has.filter.HasBooleanFilterData;
import lib.util.coordinate.Coordinate;

public class PileupData
extends AbstractData
implements HasPileupCount, HasBooleanFilterData, HasBaseCallCountFilterData {

	private PileupCount pileupCount;

	private AbstractFilteredData<BaseCallCount> baseCallCountFilterData;
	private AbstractFilteredData<Boolean> booleanFilterData;
	
	public PileupData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		super(libraryType, coordinate);

		pileupCount 		= new PileupCount((byte)'N', new ArrayBaseCallCount(), new MapBaseCallQualitityCount());

		baseCallCountFilterData	= new BaseCallFilteredData();
		booleanFilterData		= new BooleanFilteredData();
	}
	
	public PileupData(final PileupData src) {
		this(src.getLibraryType(), src.getCoordinate());
		
		pileupCount				= src.pileupCount.copy();
		baseCallCountFilterData = src.baseCallCountFilterData.copy();
		booleanFilterData		= src.booleanFilterData.copy();
	}
	
	@Override
	public PileupCount getPileupCount() {
		return pileupCount;
	}
	
	@Override
	public void setPileupCount(PileupCount pileupCount) {
		this.pileupCount = pileupCount;
	}
	
	@Override
	public PileupData copy() {
		return new PileupData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		if (getLibraryType() != null) {
			sb.append(getLibraryType().toString());
		} else {
			sb.append("null");
		}
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getPileupCount().toString());
		return sb.toString();
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return getPileupCount().getBaseCallCount();
	}

	@Override
	public int getCoverage() {
		return getPileupCount().getCoverage();
	}

	@Override
	public byte getReferenceBase() {
		return getPileupCount().getReferenceBase();
	}

	@Override
	public void setReferenceBase(final byte referenceBase) {
		getPileupCount().setReferenceBase(referenceBase);
	}

	@Override
	public AbstractFilteredData<Boolean> getBooleanFilterData() {
		return booleanFilterData;
	}
	
	@Override
	public AbstractFilteredData<BaseCallCount> getBaseCallCountFilterData() {
		return baseCallCountFilterData;
	}

	public void merge(final PileupData src) {
		pileupCount.setReferenceBase(src.getReferenceBase());
		pileupCount.merge(src.getPileupCount());
		// filter related
		baseCallCountFilterData.merge(src.baseCallCountFilterData);
		booleanFilterData.merge(src.booleanFilterData);
	}
	
}
