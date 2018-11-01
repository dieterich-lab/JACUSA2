package jacusa.filter;

import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class HomozygousFilter 
extends AbstractFilter {

	private final int homozygousConditionIndex;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public HomozygousFilter(
			final char c,
			final int homozygousConditionIndex,
			final Fetcher<BaseCallCount> bccFetcher) {

		super(c);
		
		this.homozygousConditionIndex = homozygousConditionIndex;
		this.bccFetcher = bccFetcher;
	}

	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataTypeContainer container = parallelData.getPooledData(homozygousConditionIndex - 1);
		final int alleles = bccFetcher.fetch(container).getAlleles().size();
		return alleles > 1;
	}
	
	@Override
	public int getOverhang() { 
		return 0; 
	}


}
