package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.AbstractBaseCallDataFilter;
import jacusa.filter.cache.UniqueFilterCacheWrapper;
import jacusa.filter.cache.FilterCache;
import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.region.AbstractUniqueBaseCallRegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasINDEL_FilterData;
import lib.io.ResultWriterUtils;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class INDEL_FilterFactory<T extends AbstractData & HasBaseCallCount & HasINDEL_FilterData & HasReferenceBase> 
extends AbstractDistanceFilterFactory<T> {

	public INDEL_FilterFactory() {
		super('I', 
				"Filter potential false positive variants adjacent to INDEL position(s)", 
				6, 0.5);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final AbstractBaseCallDataFilter<T> dataFilter = 
				new AbstractBaseCallDataFilter<T>(getC(), 
						getDistance(), new FilterRatio(getMinRatio()), 
						parameter, conditionFilterCaches) {
			@Override
			public BaseCallCount getBaseCallFilterCount(ParallelData<T> parallelData, int conditionIndex,
					int replicateIndex) {
				return parallelData.getData(conditionIndex, replicateIndex).getINDEL_FilterData();
			}
		};
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}

	@Override
	protected FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		// save classes
		final AbstractUniqueBaseCallRegionDataCache<T> uniqueBaseCallCache = 
			new AbstractUniqueBaseCallRegionDataCache<T>(conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), baseCallConfig, coordinateController) {
			
			@Override
			public void addData(final T data, final Coordinate coordinate) {
				final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
				if (getCoverage()[windowPosition] == 0) {
					return;
				}
				final BaseCallCount baseCallCount = new BaseCallCount();
				data.setINDEL_DistanceFilterData(baseCallCount);
				add(windowPosition, coordinate.getStrand(), baseCallCount);				
			}
		};		
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueBaseCallCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueBaseCallCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueBaseCallCache, processRecords);
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		ResultWriterUtils.addBaseCallCount(sb, data.getINDEL_FilterData());
	}	
}