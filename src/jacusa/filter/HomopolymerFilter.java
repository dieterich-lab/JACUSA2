package jacusa.filter;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.fetcher.Fetcher;
import lib.data.filter.BooleanData;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives.
 * May need data that is outside a thread window 
 */
public class HomopolymerFilter extends AbstractFilter {

	// where to store if a site is a homopolymer
	private final Fetcher<BooleanData> fetcher;
	
	public HomopolymerFilter(
			final char c, 
			final int overhang, 
			final Fetcher<BooleanData> fetcher) {
		
		super(c, overhang);
		this.fetcher = fetcher;
	}

	/**
	 * Tested in test.jacusa.filter.HomopolymerFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		// if in any condition there is a homopolyer mark site as such
		final DataContainer dataContainer 	= parallelData.getCombinedPooledData();
		final BooleanData bw 				= fetcher.fetch(dataContainer);
		return bw != null && bw.getValue();
	}
	
}
