package jacusa.filter;

import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.filter.BooleanWrapper;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives. 
 * 
 * @param 
 */
public class HomopolymerFilter 
extends AbstractFilter {

	private final Fetcher<BooleanWrapper> fetcher;
	
	public HomopolymerFilter(final char c, final int overhang, final Fetcher<BooleanWrapper> fetcher) {
		super(c, overhang);
		this.fetcher = fetcher;
	}

	@Override
	protected boolean filter(final ParallelData parallelData) {
		return fetcher.fetch(parallelData.getCombinedPooledData()).getValue();
	}
	
}
