package jacusa.filter.counts;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;

public class MinCountFilter<T extends BaseQualData> 
extends AbstractCountFilter<T> {

	private double minCount;
	
	public MinCountFilter( 
			final double minCount, 
			final AbstractParameters<T> parameters) {
		super(parameters);
		this.minCount = minCount;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelPileupData<T> parallelData, 
			final BaseQualData[][] baseQualData) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getBaseCount(variantBaseIndex);
		if (count == 0) {
			return false;
		}

		ParallelPileupData<T> filteredParallelData = applyFilter(variantBaseIndex, parallelData, baseQualData);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getBaseCount(variantBaseIndex);

		return count - filteredCount >= minCount;
	}

	public double getMinCount() {
		return minCount;
	}
	
}
