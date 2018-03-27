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
import lib.data.BaseCallCount;
import lib.data.builder.ConditionContainer;
import lib.data.cache.region.AbstractUniqueBaseCallRegionDataCache;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LRTarrestCombinedFilterFactory<T extends AbstractData & hasReferenceBase & hasLRTarrestCount> 
extends AbstractDistanceFilterFactory<T> {

	public LRTarrestCombinedFilterFactory() {
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
				// TODO 
				add(windowPosition, coordinate.getStrand(), baseCallCount);				
			}
		};

		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueBaseCallCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueBaseCallCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), uniqueBaseCallCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueBaseCallCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueBaseCallCache, processRecords);
	}

	/*
	protected UniqueRef2BaseCallDataCache<T> createUniqueBaseCallCache(
			final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		return new UniqueRef2BaseCallDataCache<T>(
				conditionParameter.getLibraryType(),
				baseCallConfig, 
				coordinateController);
	}
	*/
	
}