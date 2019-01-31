package jacusa.filter;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

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
	
	/**
	 * Tested in test.jacusa.filter.MaxAlleleFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataContainer container = parallelData.getCombinedPooledData();
		final int alleles = bccFetcher.fetch(container).getAlleles().size();
		return alleles > maxAlleles;
	}

	@Override
	public int getOverhang() { 
		return 0;
	}

}
