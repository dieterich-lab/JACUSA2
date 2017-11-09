package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameter;
import lib.data.ParallelData;
import lib.data.basecall.PileupData;

public class RatioCountFilter<T extends PileupData> 
extends AbstractCountFilter<T> {

	private double minRatio;

	public RatioCountFilter(final double minRatio, final AbstractParameter<T> parameters) {
		super(parameters);
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final PileupData[][] baseQualCounts) {
		int count = parallelData
				.getCombinedPooledData()
				.getPileupCount()
				.getBaseCount(variantBaseIndex);
		ParallelData<T> filteredParallelData = 
				applyFilter(variantBaseIndex, parallelData, baseQualCounts);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getPileupCount()
				.getBaseCount(variantBaseIndex);

		return (double)filteredCount / (double)count <= minRatio;
	}

	public double getMinRatio() {
		return minRatio;
	}

}