package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.cache.FRPairedEnd1Cache;
import lib.data.cache.FRPairedEnd2Cache;
import lib.data.has.hasPileupCount;

public abstract class AbstractStrandedDataBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilderFactory<T> {

	public AbstractStrandedDataBuilderFactory(final AbstractParameter<T> generalParameter) {
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
			
		case MIXED:
			throw new IllegalArgumentException("Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}
		
		return caches;
	}
	
	protected abstract Cache<T> createCache(final AbstractConditionParameter<T> conditionParameter);
	
}
