package jacusa.filter.factory.distance;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;

import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.AbstractLRTarrestRef2BaseCallDataFilter;
import jacusa.filter.basecall.BaseCallCountFilter;
import jacusa.filter.cache.UniqueFilterCacheWrapper;
import jacusa.filter.cache.FilterCache;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.lrtarrest.AbstractUniqueLRTarrest2BaseCallCountDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasLRTarrestSpliceSiteFilteredData;
import lib.io.ResultWriterUtils;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LRTarrestSpliceSiteFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasLRTarrestSpliceSiteFilteredData> 
extends AbstractDistanceFilterFactory<T> {

	public LRTarrestSpliceSiteFilterFactory() {
		super('S', 
				"Filter artefacts around splice site of read arrest positions.", 
				6, 0.5);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<T>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final FilterRatio filterRatio = new FilterRatio(getMinRatio());
		final AbstractLRTarrestRef2BaseCallDataFilter<T> dataFilter =
				new AbstractLRTarrestRef2BaseCallDataFilter<T>(getC(), 
						getDistance(), new BaseCallCountFilter<BaseCallData>(filterRatio), 
						parameter, conditionFilterCaches) {
			@Override
			protected Map<Integer, BaseCallCount> getFilteredData(
					ParallelData<T> parallelData,
					int conditionIndex, int replicateIndex) {
				return parallelData.getData(conditionIndex, replicateIndex).getLRTarrestSpliceSiteFilteredData();
			}
		};
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	@Override
	protected FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		final AbstractUniqueLRTarrest2BaseCallCountDataCache<T> uniqueCache = 
				new AbstractUniqueLRTarrest2BaseCallCountDataCache<T>(
						conditionParameter.getLibraryType(), conditionParameter.getMinBASQ(), 
						baseCallConfig, coordinateController) {
			@Override
			protected Map<Integer, BaseCallCount> getRefPos2bc(final T data) {
				return data.getLRTarrestSpliceSiteFilteredData();
			}
		};
		
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), uniqueCache));

		return new UniqueFilterCacheWrapper<T>(getC(), uniqueCache, processRecords);
	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		ResultWriterUtils.addResultRefPos2baseChange(sb, data.getLRTarrestSpliceSiteFilteredData());
	}
	
}