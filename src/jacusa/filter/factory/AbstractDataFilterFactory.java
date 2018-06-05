package jacusa.filter.factory;

import org.apache.commons.cli.Option;

import jacusa.filter.AbstractFilter;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 *  
 * @param <T>
 */
public abstract class AbstractDataFilterFactory<T extends AbstractData> 
extends AbstractFilterFactory<T> {

	public AbstractDataFilterFactory(final Option option) {
		super(option);
	}
	
	@Override
	public final void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addDataFilterFactory(this);
		conditionContainer.getFilterContainer().addFilter(createFilter(coordinateController, conditionContainer));
	}

	protected abstract AbstractFilter<T> createFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer); 
	
	/**
	 * TODO add comments.
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	public abstract RecordWrapperDataCache<T> createFilterCache(
			final AbstractConditionParameter<T> conditionParameter,
			final CoordinateController coordinateController);

} 