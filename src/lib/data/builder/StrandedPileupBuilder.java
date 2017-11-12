package lib.data.builder;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.has.hasPileupCount;
import lib.util.Coordinate;

public class StrandedPileupBuilder<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilder<T> {
	
	public StrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final LIBRARY_TYPE libraryType,
			final Cache<T> cache,
			FilterContainer<T> filterContainer) {
		super(conditionParameter, libraryType, cache, filterContainer);
	}
	
	@Override
	public T getData(final Coordinate coordinate) {
		return getCache().getData(coordinate);
	}
	
}