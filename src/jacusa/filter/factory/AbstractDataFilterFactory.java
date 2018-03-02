package jacusa.filter.factory;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractDataFilterFactory<T extends AbstractData> 
extends AbstractFilterFactory<T> {

	public AbstractDataFilterFactory(final char c, final String desc) {
		super(c, desc);
	}

	protected abstract FilterCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController);
	
	protected List<List<FilterCache<T>>> createConditionFilterCaches(final AbstractParameter<T, ?> parameter,
			final CoordinateController coordinateContainer,
			final AbstractDataFilterFactory<T> dataFilterFactory) {

		final List<List<FilterCache<T>>> filterCaches = 
				new ArrayList<List<FilterCache<T>>>(parameter.getConditionsSize());

		for (int conditionIndex = 0; conditionIndex < parameter.getConditionsSize(); conditionIndex++) {
			final int replicates = parameter.getReplicates(conditionIndex);
			List<FilterCache<T>> list = new ArrayList<FilterCache<T>>(replicates);
		
			for (int replicateIndex = 0; replicateIndex < parameter.getConditionsSize(); replicateIndex++) {
				final FilterCache<T> filterCache = createFilterCache(parameter.getConditionParameter(conditionIndex),
						parameter.getBaseConfig(),
						coordinateContainer);
				list.add(filterCache);
			}
			filterCaches.add(list);
		}

		return filterCaches;
	}

} 