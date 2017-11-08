package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.ParallelData;

public class CombinedCountFilter<T extends BaseQualData> 
extends AbstractCountFilter<T> {

	private RatioCountFilter<T> minRatioFilter;
	private MinCountFilter<T> minCountFilter;
	
	public CombinedCountFilter(
			final double minRatio, final double minCount,
			final AbstractParameters<T> parameters) {
		super(parameters);
		
		minRatioFilter = new RatioCountFilter<T>(minRatio, parameters);
		minCountFilter = new MinCountFilter<T>(minRatio, parameters);
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final BaseQualData[][] baseQualData) {
		return minCountFilter.filter(variantBaseIndex, parallelData, baseQualData) &&
				minRatioFilter.filter(variantBaseIndex, parallelData, baseQualData);
	}

	
}