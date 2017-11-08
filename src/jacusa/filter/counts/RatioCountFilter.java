package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.ParallelData;

public class RatioCountFilter<T extends BaseQualData> 
extends AbstractCountFilter<T> {

	private double minRatio;

	public RatioCountFilter(final double minRatio, final AbstractParameters<T> parameters) {
		super(parameters);
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final BaseQualData[][] baseQualCounts) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getBaseCount(variantBaseIndex);
		ParallelData<T> filteredParallelData = 
				applyFilter(variantBaseIndex, parallelData, baseQualCounts);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getBaseCount(variantBaseIndex);

		return (double)filteredCount / (double)count <= minRatio;
	}

	public double getMinRatio() {
		return minRatio;
	}

}