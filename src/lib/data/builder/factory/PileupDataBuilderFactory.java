package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.cache.FRPairedEnd1Cache;
import lib.data.cache.FRPairedEnd2Cache;
import lib.data.cache.PileupCountCache;
import lib.data.has.hasPileupCount;

public class PileupDataBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilderFactory<T> {

	public PileupDataBuilderFactory(final AbstractParameter<T> generalParameter) {
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
		return new PileupCountCache<T>(conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), getGeneralParameter().getBaseConfig());
	}
	
}
