package jacusa.filter;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.filter.BooleanData;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives.
 * May need data that is outside a thread window 
 */
public class HomopolymerFilter extends AbstractFilter {

	// where to store if a site is a homopolymer
	private final DataType<BooleanData> dataType;
	
	public HomopolymerFilter(
			final char id, 
			final int overhang, 
			final DataType<BooleanData> dataType) {
		
		super(id, overhang);
		this.dataType = dataType;
	}

	/**
	 * Tested in test.jacusa.filter.HomopolymerFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		// combine conditions - one condition with a homopolymer suffices for filtering
		final DataContainer dataContainer 	= parallelData.getCombPooledData();
		final BooleanData booleanData = dataContainer.get(dataType);
		
		return booleanData != null && booleanData.getValue();
	}
	
}
