package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameter;
import lib.data.ParallelData;
import lib.data.basecall.PileupData;

public class MinCountFilter<T extends PileupData> 
extends AbstractCountFilter<T> {

	private double minCount;
	
	public MinCountFilter( 
			final double minCount, 
			final AbstractParameter<T> parameters) {
		super(parameters);
		this.minCount = minCount;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final PileupData[][] baseQualData) {
		int count = parallelData
				.getCombinedPooledData()
				.getPileupCount()
				.getBaseCount(variantBaseIndex);
		if (count == 0) {
			return false;
		}

		ParallelData<T> filteredParallelData = applyFilter(variantBaseIndex, parallelData, baseQualData);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getPileupCount()
				.getBaseCount(variantBaseIndex);

		return count - filteredCount >= minCount;
	}

	public double getMinCount() {
		return minCount;
	}
	
}
