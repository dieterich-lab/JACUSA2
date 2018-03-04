package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.basecall.INDEL_BaseCallDataFilter;
import jacusa.filter.cache.UniqueFilterCacheWrapper;
import jacusa.filter.cache.FilterCache;
import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.UniqueBaseCallDataCache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.data.has.filter.hasINDEL_DistanceFilterData;
import lib.util.coordinate.CoordinateController;

public class INDEL_DistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasINDEL_DistanceFilterData & hasReferenceBase> 
extends AbstractBaseCallDistanceFilterFactory<T> {

	public INDEL_DistanceFilterFactory() {
		super('I', 
				"Filter potential false positive variants adjacent to INDEL position(s)", 
				6, 0.2, 2);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final INDEL_BaseCallDataFilter<T> dataFilter = 
				new INDEL_BaseCallDataFilter<T>(getC(), 
						getDistance(), getMinCount(), getMinRatio(), 
						parameter, conditionFilterCaches);
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}

	@Override
	protected FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final UniqueBaseCallDataCache<T> uniqueBaseCallCache = createUniqueBaseCallCache(conditionParameter, baseCallConfig, coordinateController);
		
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueBaseCallCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueBaseCallCache));

		final UniqueFilterCacheWrapper<T> distanceFilterCache = new UniqueFilterCacheWrapper<T>(getC(), uniqueBaseCallCache, processRecords);
		return distanceFilterCache;
	}
	
}