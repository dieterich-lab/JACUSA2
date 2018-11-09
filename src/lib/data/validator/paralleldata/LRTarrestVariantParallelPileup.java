package lib.data.validator.paralleldata;

import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class LRTarrestVariantParallelPileup
implements ParallelDataValidator {

	private final Fetcher<Position2baseCallCount> arrestPos2BccFetcher;
	
	public LRTarrestVariantParallelPileup(final Fetcher<Position2baseCallCount> arrestPos2BccFetcher) {
		
		this.arrestPos2BccFetcher = arrestPos2BccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataTypeContainer combinedPooledContainer = parallelData.getCombinedPooledData();
		
		final Position2baseCallCount arrestPos2Bcc = 
				arrestPos2BccFetcher.fetch(combinedPooledContainer);
		
		// TODO check
		final BaseCallCount totalBcc = combinedPooledContainer.getBaseCallCount();
		final BaseCallCount arrestBcc = arrestPos2Bcc.getTotalBaseCallCount();
		final BaseCallCount throughBcc = totalBcc.copy().subtract(arrestBcc);

		return arrestBcc.getCoverage() > 0 && throughBcc.getCoverage() > 0;
	}

}