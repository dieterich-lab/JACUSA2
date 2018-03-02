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
import lib.data.generator.DataGenerator;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class LinkedCombinedDistanceFilterFactory<T extends AbstractData & hasReferenceBase & hasLRTarrestCount, F extends AbstractData & hasReferenceBase & hasLRTarrestCount> 
extends AbstractDistanceFilterFactory<T, F> {

	public LinkedCombinedDistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('D', "Filter distance to TODO position.", 5, 0.5, 1, dataGenerator);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<F>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final Ref2BaseCallDataFilter<T, F> dataFilter = 
				new Ref2BaseCallDataFilter<T, F>(getC(), 
						getDistance(), getMinCount(), getMinRatio(), 
						parameter, this, conditionFilterCaches);
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	@Override
	protected FilterCache<F> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final UniqueRef2BaseCallDataCache<F> uniqueDataCache = createUniqueBaseCallCache(conditionParameter, baseCallConfig, coordinateController);

		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueDataCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), uniqueDataCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueDataCache));

		return new UniqueFilterCacheWrapper<F>(getC(), uniqueDataCache, processRecords);
	}

	protected UniqueRef2BaseCallDataCache<F> createUniqueBaseCallCache(
			final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		return new UniqueRef2BaseCallDataCache<F>(
				conditionParameter.getLibraryType(),
				baseCallConfig, 
				coordinateController);
	}
	
}