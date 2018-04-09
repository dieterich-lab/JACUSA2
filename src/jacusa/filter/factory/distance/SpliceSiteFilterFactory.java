	package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.basecall.SpliceSiteDataFilter;
import jacusa.filter.cache.UniqueFilterCacheWrapper;
import jacusa.filter.cache.FilterCache;
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
import lib.data.has.filter.HasSpliceSiteFilterData;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 */
public class SpliceSiteFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasSpliceSiteFilterData>
extends AbstractDistanceFilterFactory<T> {

	public SpliceSiteFilterFactory() {
		super('S', 
				"Filter potential false positive variants adjacent to splice site(s).", 
				6, 0.5, 2);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = 
				createConditionFilterCaches(parameter, coordinateController, this);
		final SpliceSiteDataFilter<T> dataFilter = 
				new SpliceSiteDataFilter<T>(getC(), 
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
				data.setSpliceSiteDistanceFilterData(baseCallCount);
				add(windowPosition, coordinate.getStrand(), baseCallCount);				
			}
		};
		
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueBaseCallCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueBaseCallCache, processRecords);
	}

}