package jacusa.filter.basecall;

import jacusa.filter.cache.FilterCache;

import java.util.List;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.filter.hasReadPositionDistanceFilterData;

public class ReadPositionBaseCallDataFilter<T extends AbstractData & hasBaseCallCount & hasReadPositionDistanceFilterData> 
extends AbstractBaseCallDataFilter<T> {

	public ReadPositionBaseCallDataFilter(final char c, 
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
		return parallelData.getData(conditionIndex, replicateIndex).getReadPositionDistanceFilterData();
	}

}
