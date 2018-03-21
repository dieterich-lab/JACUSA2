package jacusa.filter.basecall;

import java.util.List;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;

/**
 * Abstract class that enables filtering based on base call count data and some other filter chached data.
 * 
 * @param <T>
 */
public abstract class AbstractBaseCallDataFilter<T extends AbstractData & hasBaseCallCount> 
extends AbstractDataFilter<T> {

	private final int minCount;
	private final double minRatio;

	public AbstractBaseCallDataFilter(final char c, 
			final int overhang, 
			final int minCount, final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {
		
		super(c, overhang, parameter, conditionFilterCaches);

		this.minCount = minCount;
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);

		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				final int replicates = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// observed count
					final T data = parallelData.getData(conditionIndex, replicateIndex);
					count += data.getBaseCallCount().getBaseCallCount(variantBaseIndex);
					// possible artefact count
					filteredCount += getBaseCallFilterData(parallelData, conditionIndex, replicateIndex)
							.getBaseCallCount(variantBaseIndex);
				}
			}

			// check if too much filteredCount
			if (filter(count, filteredCount)) {
				return true;
			}
			
		}

		return false;
	}

	/**
	 * Returns a BaseCallCount object for a specific condition and replicate.
	 * 
	 * @param parallelData		the data to extract the BaseCallCount object from 
	 * @param conditionIndex	the condition
	 * @param replicateIndex	the replicate
	 * @return a BaseCallCount object
	 */
	protected abstract BaseCallCount getBaseCallFilterData(ParallelData<T> parallelData, 
			int conditionIndex, int replicateIndex);

	/**
	 * TODO add comments
	 * 
	 * @param count			observed count
	 * @param filteredCount processed filtered count
	 * @return  
	 */
	protected boolean filter(final int count, final int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio || count - filteredCount >= minCount;
	}
	
}
