package lib.data.builder;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.has.hasPileupCount;
import lib.util.Coordinate;

public class UnstrandedPileupBuilder<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilder<T> {
	
	public UnstrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final Cache<T> cache,
			final FilterContainer<T> filterContainer) {
		super(conditionParameter, LIBRARY_TYPE.UNSTRANDED, cache, filterContainer);
	}

	@Override
	public T getData(final Coordinate coordinate) {
		return getCache().getData(coordinate);
	}
	
}