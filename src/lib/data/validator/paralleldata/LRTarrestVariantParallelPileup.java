package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;

public class LRTarrestVariantParallelPileup
implements ParallelDataValidator {

	private final Fetcher<ArrestPosition2baseCallCount> arrestPos2BccFetcher;
	
	public LRTarrestVariantParallelPileup(final Fetcher<ArrestPosition2baseCallCount> arrestPos2BccFetcher) {
		
		this.arrestPos2BccFetcher = arrestPos2BccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer combinedPooledContainer = parallelData.getCombinedPooledData();
		
		final ArrestPosition2baseCallCount ap2bcc = 
				arrestPos2BccFetcher.fetch(combinedPooledContainer);
		
		final int onePosition = parallelData.getCoordinate().get1Position();
		final BaseCallCount arrestBcc = ap2bcc.getArrestBaseCallCount(onePosition);
		final BaseCallCount throughBcc = ap2bcc.getThroughBaseCallCount(onePosition);

		return arrestBcc.getCoverage() > 0 && throughBcc.getCoverage() > 0;
	}

}