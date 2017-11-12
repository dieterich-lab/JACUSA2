package lib.data.builder.factory;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.UnstrandedPileupBuilder;
import lib.data.cache.Cache;
import lib.data.cache.PileupCountCache;
import lib.data.has.hasPileupCount;

public class UnstrandedPileupBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory() {
		this(null);
	}
	
	public UnstrandedPileupBuilderFactory(final AbstractParameter<T> generalParameter) {
		super(LIBRARY_TYPE.UNSTRANDED, generalParameter);
	}

	@Override
	public UnstrandedPileupBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) {
		final Cache<T> cache =  new PileupCountCache<T>(conditionParameter, getGeneralParameter().getMethodFactory());
		final FilterContainer<T> filterContainer = null; // TODO

		return new UnstrandedPileupBuilder<T>(conditionParameter, cache, filterContainer);
	}

}
