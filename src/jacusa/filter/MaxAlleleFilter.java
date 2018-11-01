package jacusa.filter;

import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class MaxAlleleFilter extends AbstractFilter {

	private final int maxAlleles;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public MaxAlleleFilter(
			final char c, 
			final int maxAlleles, 
			final Fetcher<BaseCallCount> bccFetcher) {
		
		super(c);
		this.maxAlleles = maxAlleles;
		this.bccFetcher = bccFetcher;
	}
	
	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataTypeContainer container = parallelData.getCombinedPooledData();
		final int alleles = bccFetcher.fetch(container).getAlleles().size();
		return alleles > maxAlleles;
	}

	@Override
	public int getOverhang() { 
		return 0;
	}

}
