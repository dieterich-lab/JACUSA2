package lib.data.validator.paralleldata;

import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class LRTarrestVariantParallelPileup
implements ParallelDataValidator {

	private final Fetcher<ArrestPos2BaseCallCount> arrestPos2BccFetcher;
	
	public LRTarrestVariantParallelPileup(final Fetcher<ArrestPos2BaseCallCount> arrestPos2BccFetcher) {
		
		this.arrestPos2BccFetcher = arrestPos2BccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataTypeContainer combinedPooledContainer = parallelData.getCombinedPooledData();
		final int position = parallelData.getCoordinate().getPosition();
		
		final ArrestPos2BaseCallCount arrestPos2Bcc = 
				arrestPos2BccFetcher.fetch(combinedPooledContainer);
		
		final BaseCallCount arrestBaseCallCount = arrestPos2Bcc.getArrestBaseCallCount(position);
		final BaseCallCount throughBaseCallCount = arrestPos2Bcc.getThroughBaseCallCount(position);

		return arrestBaseCallCount.getCoverage() > 0 && throughBaseCallCount.getCoverage() > 0;
	}

}