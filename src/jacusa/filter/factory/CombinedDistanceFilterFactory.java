package jacusa.filter.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.BaseCallDataFilter;
import jacusa.filter.cache.DistanceFilterCache;
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
import lib.data.cache.UniqueBaseCallDataCache;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class CombinedDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractDistanceFilterFactory<T, F> {

	public CombinedDistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('D', "Filter distance to TODO position.", 5, 0.5, 1, dataGenerator);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<F>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final BaseCallDataFilter<T, F> dataFilter = 
				new BaseCallDataFilter<T, F>(getC(), 
						getDistance(), getMinCount(), getMinRatio(), 
						parameter, this, conditionFilterCaches);
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	@Override
	protected FilterCache<F> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final UniqueBaseCallDataCache<F> uniqueBaseCallCache = createUniqueBaseCallCache(conditionParameter, baseCallConfig, coordinateController);
		
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueBaseCallCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueBaseCallCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), uniqueBaseCallCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueBaseCallCache));

		final DistanceFilterCache<F> distanceFilterCache = new DistanceFilterCache<F>(getC(), uniqueBaseCallCache, processRecords);
		return distanceFilterCache;
	}

}