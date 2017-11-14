package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.BaseCallCache;
import lib.data.cache.Cache;
import lib.data.cache.FRPairedEnd1Cache;
import lib.data.cache.FRPairedEnd2Cache;
import lib.data.has.hasBaseCallCount;

public class BaseCallDataBuilderFactory<T extends AbstractData & hasBaseCallCount> 
extends AbstractDataBuilderFactory<T> {

	public BaseCallDataBuilderFactory(final AbstractParameter<T> generalParameter) {
		super(generalParameter);
	}
	
	public List<Cache<T>> createCaches(final AbstractConditionParameter<T> conditionParameter) {
		final List<Cache<T>> caches = new ArrayList<Cache<T>>(2);

		switch (conditionParameter.getLibraryType()) {
		case FR_FIRSTSTRAND:
			caches.add(new FRPairedEnd1Cache<T>(createCache(conditionParameter), createCache(conditionParameter)));
			break;
		
		case FR_SECONDSTRAND:
			caches.add(new FRPairedEnd2Cache<T>(createCache(conditionParameter), createCache(conditionParameter)));
			break;
			
		case UNSTRANDED:
			caches.add(createCache(conditionParameter));
			break;
		}
		
		return caches;
	}
	
	private Cache<T> createCache(final AbstractConditionParameter<T> conditionParameter) {
		return new BaseCallCache<T>(conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), getGeneralParameter().getBaseConfig());
	}
	
}
