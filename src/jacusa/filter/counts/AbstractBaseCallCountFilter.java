package jacusa.filter.counts;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public abstract class AbstractBaseCallCountFilter<T extends AbstractData & hasReferenceBase & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> {

	private final AbstractFilterFactory<T, F> filterFactory;

	public AbstractBaseCallCountFilter(final AbstractFilterFactory<T, F> filterFactory) {
		
		this.filterFactory = filterFactory;
	}

	// ORDER RESULTS [0] SHOULD BE THE VARIANTs TO TEST
	public int[] getVariantBaseIndexs(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final byte referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
		final int[] alleles = parallelData.getCombinedPooledData().getBaseCallCount().getAlleles();

		int[] observedAlleleCount = new int[BaseCallConfig.BASES.length];
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			for (int baseIndex : parallelData.getPooledData(conditionIndex).getBaseCallCount().getAlleles()) {
				observedAlleleCount[baseIndex]++;
			}
		}

		// A | G
		// define all non-reference base as potential variants
		if (alleles.length == 2 && 
				observedAlleleCount[alleles[0]] + observedAlleleCount[alleles[1]] == conditions) {
			// define non-reference base as potential variants
			if (referenceBase == 'N') {
				return new int[0];
			}
			
			final int referenceBaseIndex = BaseCallConfig.getInstance().getBaseIndex((byte)referenceBase);
			for (final int baseIndex : alleles) {
				if (baseIndex != referenceBaseIndex) {
					return new int[] {baseIndex};
				}
			}
		}
		
		// A | AG
		if (alleles.length == 2 && 
				observedAlleleCount[alleles[0]] + observedAlleleCount[alleles[1]] > conditions) {
			return ParallelData.getVariantBaseIndexs(parallelData);
		}

		// condition1: AG | AG AND condition2: AGC |AGC
		// return allelesIs;
		return new int[0];
	}

	protected boolean applyFilter(final int variantBaseIndex, final T[] data, final F[] storageFilterData, final F[] filteredData) {
		// indicates if something has been filtered
		boolean processed = false;
		
		for (int replicateIndex = 0; replicateIndex < data.length; ++replicateIndex) {
			filteredData[replicateIndex].add(data[replicateIndex]); // TODO check
			
			if (storageFilterData[replicateIndex] != null) { 
				filteredData[replicateIndex].getBaseCallCount()
					.substract(variantBaseIndex, storageFilterData[replicateIndex].getBaseCallCount());
				processed = true;
			}
		}

		return processed;
	}
	
	/**
	 * null if filter did not change anything
	 */
	protected ParallelData<F> applyFilter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final ParallelData<F> parallelStorageFilterData) {

		final F[][] filteredParallelArray = filterFactory.createContainerData(parallelData.getConditions());
		int filtered = 0;
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			final F[] filteredData = filterFactory.createReplicateData(parallelData.getReplicates(conditionIndex));
					
			if (applyFilter(variantBaseIndex, 
					parallelData.getData(conditionIndex), 
					parallelStorageFilterData.getData(conditionIndex),
					filteredData)) {
				
				filtered++;
			}
			filteredParallelArray[conditionIndex] = filteredData;
		}

		if (filtered == 0) {
			// nothing has been filtered
			return null;
		}

		return new ParallelData<F>(filterFactory, parallelData.getCoordinate(), filteredParallelArray);
	}
	
	/**
	 * Apply filter on each variant base
	 */
	public boolean filter(final int[] variantBaseIndexs, 
			final ParallelData<T> parallelData, 
			final ParallelData<F> parallelStorageFilterData) {

		for (int variantBaseIndex : variantBaseIndexs) {
			if (filter(variantBaseIndex, parallelData, parallelStorageFilterData)) {
				return true;
			}
		}

		return false;
	}

	protected abstract boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final ParallelData<F> filteredParallelData);

}
