package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;

import jacusa.filter.AbstractLRTarrestRef2BaseCallDataFilter;
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
import lib.data.cache.lrtarrest.AbstractUniqueLRTarrest2BaseCallCountDataCache;
import lib.data.cache.lrtarrest.INDEL_LRTarrest2BaseCallCountDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasLRTarrestINDEL_FilteredData;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LRTarrestINDEL_FilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasLRTarrestINDEL_FilteredData> 
extends AbstractDistanceFilterFactory<T> {

	public LRTarrestINDEL_FilterFactory() {
		super('D', 
				"Filter artefacts around INDELs of read arrest positions.", 
				5, 0.5, 1);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final AbstractLRTarrestRef2BaseCallDataFilter<T> dataFilter = 
				new AbstractLRTarrestRef2BaseCallDataFilter<T>(getC(), 
						getDistance(), getMinCount(), getMinRatio(), 
						parameter, conditionFilterCaches) {
				@Override
				protected Map<Integer, BaseCallCount> getFilteredData(
						ParallelData<T> parallelData,
						int conditionIndex, int replicateIndex) {
					return parallelData.getData(conditionIndex, replicateIndex).getLRTarrestINDEL_FilteredData();
				}
			};
		
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	@Override
	protected FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final AbstractUniqueLRTarrest2BaseCallCountDataCache<T> uniqueCache = 
				new INDEL_LRTarrest2BaseCallCountDataCache<T>(
						conditionParameter.getLibraryType(), conditionParameter.getMinBASQ(), 
						baseCallConfig, coordinateController);

		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), uniqueCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), uniqueCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueCache, processRecords);
	}
	
}