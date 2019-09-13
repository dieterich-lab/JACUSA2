package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPos2BCC;

public class LRTarrestVariantParallelPileup
implements ParallelDataValidator {

	private final Fetcher<ArrestPos2BCC> arrestPos2BccFetcher;

	public LRTarrestVariantParallelPileup(final Fetcher<ArrestPos2BCC> arrestPos2BccFetcher) {
		this.arrestPos2BccFetcher = arrestPos2BccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer combinedPooledContainer = parallelData.getCombPooledData();
		
		final ArrestPos2BCC ap2bcc = 
				arrestPos2BccFetcher.fetch(combinedPooledContainer);
		
		final int onePosition = parallelData.getCoordinate().get1Position();
		final BaseCallCount arrestBcc = ap2bcc.getArrestBCC(onePosition);
		final BaseCallCount throughBcc = ap2bcc.getThroughBaseCallCount(onePosition);
		
		return arrestBcc.getCoverage() > 0 && throughBcc.getCoverage() > 0;
	}

}