package lib.data.fetcher;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;

public class Pileup2BaseCallCount implements Fetcher<BaseCallCount> {
	
	private final DataType<PileupCount> dataType;
	
	public Pileup2BaseCallCount(final DataType<PileupCount> dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public BaseCallCount fetch(final DataContainer dataContainer) {
		return dataContainer.get(dataType).getBCC();
	}
	
}
