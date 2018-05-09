package jacusa.filter.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.record.RecordDataCache;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 *  
 * @param <T>
 */
public abstract class AbstractDataFilterFactory<T extends AbstractData> 
extends AbstractFilterFactory<T> {

	public AbstractDataFilterFactory(final char c, final String desc) {
		super(c, desc);
	}

	/**
	 * TODO add comments.
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	protected abstract RecordDataCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController);
	
	/**
	 * TODO add comments.
	 * 
	 * @param parameter
	 * @param coordinateContainer
	 * @param dataFilterFactory
	 * @return
	 */
	protected List<List<RecordDataCache<T>>> createConditionFilterCaches(final AbstractParameter<T, ?> parameter,
			final CoordinateController coordinateContainer,
			final AbstractDataFilterFactory<T> dataFilterFactory) {

		// container for the result - 
		// outer list -> conditions
		// inner list -> replicates
		final List<List<RecordDataCache<T>>> filterCaches = 
				new ArrayList<List<RecordDataCache<T>>>(parameter.getConditionsSize());

		for (int conditionIndex = 0; conditionIndex < parameter.getConditionsSize(); conditionIndex++) {
			// replicates for conditionIndex
			final int replicates = parameter.getReplicates(conditionIndex);
			final List<RecordDataCache<T>> list = new ArrayList<RecordDataCache<T>>(replicates);
		
			for (int replicateIndex = 0; replicateIndex < parameter.getConditionsSize(); replicateIndex++) {
				// create filterCache 
				final RecordDataCache<T> filterCache = createFilterCache(
						parameter.getConditionParameter(conditionIndex),
						parameter.getBaseConfig(),
						coordinateContainer);
				// add to list of replicates
				list.add(filterCache);
			}
			// add list of replicates to list of conditions
			filterCaches.add(list);
		}

		return filterCaches;
	}

} 