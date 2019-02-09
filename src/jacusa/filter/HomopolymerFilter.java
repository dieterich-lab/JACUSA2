package jacusa.filter;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.fetcher.Fetcher;
import lib.data.filter.BooleanWrapper;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives. 
 * 
 * @param 
 */
public class HomopolymerFilter extends AbstractFilter {

	private final Fetcher<BooleanWrapper> fetcher;
	
	public HomopolymerFilter(
			final char c, 
			final int overhang, 
			final Fetcher<BooleanWrapper> fetcher) {
		
		super(c, overhang);
		this.fetcher = fetcher;
	}

	/**
	 * Tested in test.jacusa.filter.HomopolymerFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataContainer dataContainer 	= parallelData.getCombinedPooledData();
		final BooleanWrapper bw 			= fetcher.fetch(dataContainer);
		return bw != null && bw.getValue();
	}
	
}
