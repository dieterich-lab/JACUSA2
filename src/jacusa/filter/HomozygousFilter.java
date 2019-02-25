package jacusa.filter;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

/**
 * This class implements a filter that requires a condition to be homomorph at a site.
 */
public class HomozygousFilter extends AbstractFilter {

	// defines the conditions that requires to be homomorph
	private final int homozygousConditionIndex;
	// defines what base call counts to use for filtering
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public HomozygousFilter(
			final char c,
			final int homozygousConditionIndex,
			final Fetcher<BaseCallCount> bccFetcher) {

		super(c);
		
		this.homozygousConditionIndex = homozygousConditionIndex;
		this.bccFetcher = bccFetcher;
	}

	/**
	 * Tested in test.jacusa.filter.HomozygousFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataContainer container = parallelData.getPooledData(homozygousConditionIndex);
		final int alleles = bccFetcher.fetch(container).getAlleles().size();
		return alleles > 1;
	}
	
	@Override
	public int getOverhang() { 
		return 0; 
	}


}
