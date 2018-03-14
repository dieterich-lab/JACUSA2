package jacusa.filter.factory;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
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
	protected abstract FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
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
	protected List<List<FilterCache<T>>> createConditionFilterCaches(final AbstractParameter<T, ?> parameter,
			final CoordinateController coordinateContainer,
			final AbstractDataFilterFactory<T> dataFilterFactory) {

		// container for the result - 
		// outer list -> conditions
		// inner list -> replicates
		final List<List<FilterCache<T>>> filterCaches = 
				new ArrayList<List<FilterCache<T>>>(parameter.getConditionsSize());

		for (int conditionIndex = 0; conditionIndex < parameter.getConditionsSize(); conditionIndex++) {
			// replicates for conditionIndex
			final int replicates = parameter.getReplicates(conditionIndex);
			final List<FilterCache<T>> list = new ArrayList<FilterCache<T>>(replicates);
		
			for (int replicateIndex = 0; replicateIndex < parameter.getConditionsSize(); replicateIndex++) {
				// create filterCache 
				final FilterCache<T> filterCache = createFilterCache(
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