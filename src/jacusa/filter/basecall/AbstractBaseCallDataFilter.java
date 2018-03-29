package jacusa.filter.basecall;

import java.util.List;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;

/**
 * Abstract class that enables filtering based on base call count data and some other filter chached data.
 * 
 * @param <T>
 */
public abstract class AbstractBaseCallDataFilter<T extends AbstractData & HasReferenceBase & HasBaseCallCount> 
extends AbstractDataFilter<T> {

	// FIXME use
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
		// final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);
		final int[] variantBaseIndexs = ParallelData.getNonReferenceBaseIndexs(parallelData);

		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				final int replicates = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// observed count
					final T data = parallelData.getData(conditionIndex, replicateIndex);
					final int tmpCount = data.getBaseCallCount().getBaseCallCount(variantBaseIndex);
					count += tmpCount;
					// possible artefact count
					final BaseCallCount filteredBaseCallCount = getBaseCallFilterData(parallelData, conditionIndex, replicateIndex);
					if (filteredBaseCallCount != null) {
						filteredCount += tmpCount - filteredBaseCallCount.getBaseCallCount(variantBaseIndex);						
					} else {
						filteredCount += tmpCount;
					}
				}
			}

			/*
			final String s = parallelData.getCombinedPooledData().getCoordinate().toString();
			int alleles = parallelData.getCombinedPooledData().getBaseCallCount().getAlleles().length;
			System.out.println(s + "\t" + count + "\t" + filteredCount + "\t" + (double)filteredCount / (double)count + "\t" + variantBaseIndexs.length + "\t" + alleles);
			*/
			// check if too much filteredCount
			if (filter(count, filteredCount)) {
				// FIXME int referencePosition = parallelData.getCoordinate().getPosition();
				// FIXME int windowPosition = filterCaches.get(0).get(0).getCoordinateController().convert2windowPosition(parallelData.getCoordinate());
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
		return (double)filteredCount / (double)count <= minRatio;
		// FIXME && filteredCount >= minCount;
	}
	
}
