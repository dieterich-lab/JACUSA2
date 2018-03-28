package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.basecall.CombinedDataFilter;
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
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasCombindedFilterData;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class CombinedFilterFactory<T extends AbstractData & HasBaseCallCount & HasCombindedFilterData & HasReferenceBase> 
extends AbstractDistanceFilterFactory<T> {

	public CombinedFilterFactory() {
		super('D', 
				"Filter artefacts in the vicinity of read start/end, INDELs, and splice site position(s)", 
				5, 0.5, 1);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final CombinedDataFilter<T> dataFilter = 
				new CombinedDataFilter<T>(getC(), 
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
				data.setCombinedDistanceFilterData(baseCallCount);
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

		final UniqueFilterCacheWrapper<T> distanceFilterCache = new UniqueFilterCacheWrapper<T>(getC(), uniqueBaseCallCache, processRecords);
		return distanceFilterCache;
	}

}