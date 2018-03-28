package jacusa.filter.basecall;

import jacusa.filter.cache.FilterCache;

import java.util.List;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasReadPositionFilterData;

/**
 * TODO add comments
 * 
 * @param <T>
 */
public class ReadPositionDataFilter<T extends AbstractData & HasBaseCallCount & HasReadPositionFilterData & HasReferenceBase> 
extends AbstractBaseCallDataFilter<T> {

	public ReadPositionDataFilter(final char c, 
			final int overhang, 
			final int minCount, final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang, 
				minCount, minRatio,
				parameter, 
				conditionFilterCaches);
	}

	@Override
	protected BaseCallCount getBaseCallFilterData(final ParallelData<T> parallelData, 
			final int conditionIndex, final int replicateIndex) {

		return parallelData.getData(conditionIndex, replicateIndex).getReadPositionFilterData();
	}

}
