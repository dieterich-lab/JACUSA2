package jacusa.filter;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;

/**
 * This class implements a filter that restricts the number of observed alleles 
 * at a site.
 */
public class MaxAlleleFilter extends AbstractFilter {

	private final int maxAlleles;
	// defines what base call count to use
	private final DataType<BaseCallCount> dataType;
	
	public MaxAlleleFilter(
			final char id, 
			final int maxAlleles, 
			final DataType<BaseCallCount> dataType) {
		
		super(id);
		this.maxAlleles = maxAlleles;
		this.dataType = dataType;
	}
	
	/**
	 * Tested in test.jacusa.filter.MaxAlleleFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final DataContainer container 	= parallelData.getCombPooledData();
		final int alleles 				= container.get(dataType).getAlleles().size();

		return alleles > maxAlleles;
	}

	@Override
	public int getOverhang() { 
		return 0;
	}

}
