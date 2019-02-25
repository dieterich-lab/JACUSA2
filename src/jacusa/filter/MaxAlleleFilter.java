package jacusa.filter;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

/**
 * This class implements a filter that restricts the number of observed alleles at a site.
 */
public class MaxAlleleFilter extends AbstractFilter {

	private final int maxAlleles;
	// defines what base call count to use
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
		final DataContainer container 	= parallelData.getCombinedPooledData();
		final int alleles 				= bccFetcher.fetch(container).getAlleles().size();
		return alleles > maxAlleles;
	}

	@Override
	public int getOverhang() { 
		return 0;
	}

}
