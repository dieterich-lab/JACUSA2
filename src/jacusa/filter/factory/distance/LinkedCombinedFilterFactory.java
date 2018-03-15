package jacusa.filter.factory.distance;

import java.util.ArrayList;

import java.util.List;

import jacusa.filter.Ref2BaseCallDataFilter;
import jacusa.filter.cache.UniqueFilterCacheWrapper;
import jacusa.filter.cache.FilterCache;
import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.UniqueRef2BaseCallDataCache;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LinkedCombinedFilterFactory<T extends AbstractData & hasReferenceBase & hasLRTarrestCount & hasLRTarrestCountFilteredData> 
extends AbstractDistanceFilterFactory<T> {

	public LinkedCombinedFilterFactory() {
		super('D', 
				"Filter distance to TODO position.", 
				5, 0.5, 1);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final Ref2BaseCallDataFilter<T> dataFilter = 
				new Ref2BaseCallDataFilter<T>(getC(), 
						getDistance(), getMinCount(), getMinRatio(), 
						parameter, conditionFilterCaches);
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	@Override
	protected FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final UniqueRef2BaseCallDataCache<T> uniqueDataCache = createUniqueBaseCallCache(conditionParameter, baseCallConfig, coordinateController);

		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueDataCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), uniqueDataCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueDataCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueDataCache, processRecords);
	}

	protected UniqueRef2BaseCallDataCache<T> createUniqueBaseCallCache(
			final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		return new UniqueRef2BaseCallDataCache<T>(
				conditionParameter.getLibraryType(),
				baseCallConfig, 
				coordinateController);
	}
	
}