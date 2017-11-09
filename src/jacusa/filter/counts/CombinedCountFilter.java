package jacusa.filter.counts;

import lib.cli.parameters.AbstractParameter;
import lib.data.ParallelData;
import lib.data.basecall.PileupData;

public class CombinedCountFilter<T extends PileupData> 
extends AbstractCountFilter<T> {

	private RatioCountFilter<T> minRatioFilter;
	private MinCountFilter<T> minCountFilter;
	
	public CombinedCountFilter(
			final double minRatio, final double minCount,
			final AbstractParameter<T> parameters) {
		super(parameters);
		
		minRatioFilter = new RatioCountFilter<T>(minRatio, parameters);
		minCountFilter = new MinCountFilter<T>(minRatio, parameters);
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final PileupData[][] baseQualData) {
		return minCountFilter.filter(variantBaseIndex, parallelData, baseQualData) &&
				minRatioFilter.filter(variantBaseIndex, parallelData, baseQualData);
	}

	
}