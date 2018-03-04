package jacusa.filter.basecall;

import jacusa.filter.cache.FilterCache;

import java.util.List;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.filter.hasSpliceSiteDistanceFilterData;

public class SpliceSiteBaseCallDataFilter<T extends AbstractData & hasBaseCallCount & hasSpliceSiteDistanceFilterData> 
extends AbstractBaseCallDataFilter<T> {

	public SpliceSiteBaseCallDataFilter(final char c, 
			final int overhang, 
			final int minCount, final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, 
				overhang, 
				minCount, minRatio,
				parameter, 
				conditionFilterCaches);
	}

	@Override
	protected BaseCallCount getFilteredBaseCallData(final ParallelData<T> parallelData, final int conditionIndex, final int replicateIndex) {
		return parallelData.getData(conditionIndex, replicateIndex).getSpliceSiteDistanceFilterData();
	}

}
