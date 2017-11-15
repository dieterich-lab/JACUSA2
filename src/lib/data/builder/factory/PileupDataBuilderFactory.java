package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.cache.PileupCountCache;
import lib.data.has.hasPileupCount;

public class PileupDataBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractStrandedDataBuilderFactory<T> {

	public PileupDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected Cache<T> createCache(final AbstractConditionParameter<T> conditionParameter) {
		return new PileupCountCache<T>(conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), 
				getGeneralParameter().getBaseConfig(), getGeneralParameter().getActiveWindowSize());
	}
	
}
