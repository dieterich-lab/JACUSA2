package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseCallConfig;
import lib.data.BaseQualData;
import lib.data.ParallelData;

public abstract class AbstractCountFilter<T extends BaseQualData> {

	private AbstractParameters<T> parameters;

	public AbstractCountFilter(final AbstractParameters<T> parameters) {
		this.parameters	= parameters;
	}

	// ORDER RESULTS [0] SHOULD BE THE VARIANTs TO TEST
	public int[] getVariantBaseIndexs(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final char referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
		final int[] alleles = parallelData.getCombinedPooledData().getBaseQualCount().getAlleles();

		int[] observedAlleleCount = new int[BaseCallConfig.BASES.length];
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			for (int baseIndex : parallelData.getPooledData(conditionIndex).getBaseQualCount().getAlleles()) {
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

	protected T[] applyFilter(final int variantBaseIndex, final T[] data, final BaseQualData[] baseQualData) {
		final T[] filtered = parameters.getMethodFactory().createReplicateData(data.length);

		// indicates if something has been filtered
		boolean processed = false;
		
		for (int replicateIndex = 0; replicateIndex < data.length; ++replicateIndex) {
			filtered[replicateIndex].add(data[replicateIndex]); // TODO check
			
			if (baseQualData[replicateIndex] != null) { 
				filtered[replicateIndex].getBaseQualCount()
					.substract(variantBaseIndex, baseQualData[replicateIndex].getBaseQualCount());
				processed = true;
			}
		}

		return processed ? filtered : null;
	}
	
	/**
	 * null if filter did not change anything
	 */
	protected ParallelData<T> applyFilter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final BaseQualData[][] baseQualData) {
		
		T[][] filteredData = parameters.getMethodFactory().createContainer(parallelData.getConditions());
		int filtered = 0;
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			filteredData[conditionIndex] = 
					applyFilter(variantBaseIndex, parallelData.getData(conditionIndex), baseQualData[conditionIndex]);
			if (filteredData[conditionIndex] == null) {
				filteredData[conditionIndex] = parallelData.getData(conditionIndex);
			} else {
				filtered++;
			}
		}

		if (filtered == 0) {
			// nothing has been filtered
			return null;
		}

		final ParallelData<T> filteredParallelData =
				new ParallelData<T>(parameters.getMethodFactory(), parallelData.getCoordinate(), filteredData);

		return filteredParallelData;
	}

	/**
	 * Apply filter on each variant base
	 */
	public boolean filter(final int[] variantBaseIndexs, 
			final ParallelData<T> parallelData, 
			final BaseQualData[][] baseQualData) {

		for (int variantBaseIndex : variantBaseIndexs) {
			if (filter(variantBaseIndex, parallelData, baseQualData)) {
				return true;
			}
		}

		return false;
	}

	protected abstract boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			BaseQualData[][] baseQualData);

}
