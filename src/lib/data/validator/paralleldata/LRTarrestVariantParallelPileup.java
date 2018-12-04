package lib.data.validator.paralleldata;

import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class LRTarrestVariantParallelPileup
implements ParallelDataValidator {

	private final Fetcher<ArrestPosition2baseCallCount> arrestPos2BccFetcher;
	
	public LRTarrestVariantParallelPileup(final Fetcher<ArrestPosition2baseCallCount> arrestPos2BccFetcher) {
		
		this.arrestPos2BccFetcher = arrestPos2BccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataTypeContainer combinedPooledContainer = parallelData.getCombinedPooledData();
		
		final ArrestPosition2baseCallCount ap2bcc = 
				arrestPos2BccFetcher.fetch(combinedPooledContainer);
		
		final int position = parallelData.getCoordinate().getPosition();
		final BaseCallCount arrestBcc = ap2bcc.getArrestBaseCallCount(position);
		final BaseCallCount throughBcc = ap2bcc.getThroughBaseCallCount(position);

		return arrestBcc.getCoverage() > 0 && throughBcc.getCoverage() > 0;
	}

}