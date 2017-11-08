package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.ParallelData;

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
			final ParallelData<T> parallelData, 
			final BaseQualData[][] baseQualData) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getBaseCount(variantBaseIndex);
		if (count == 0) {
			return false;
		}

		ParallelData<T> filteredParallelData = applyFilter(variantBaseIndex, parallelData, baseQualData);
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
