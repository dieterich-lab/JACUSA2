package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class NonHomozygousSite 
implements ParallelDataValidator {

	private final Fetcher<BaseCallCount> bccFetcher;
	
	public NonHomozygousSite(final Fetcher<BaseCallCount> bccFetcher) {
		this.bccFetcher = bccFetcher;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer container = parallelData.getCombinedPooledData();
		final BaseCallCount bcc = bccFetcher.fetch(container);
		// more than one non-reference allele
		return bcc.getAlleles().size() > 1;
	}

}
