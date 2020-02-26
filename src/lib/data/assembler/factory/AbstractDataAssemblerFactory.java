package lib.data.assembler.factory;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.storage.Cache;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.FRPairedEnd2CacheContainer;
import lib.data.storage.container.RFPairedEnd1CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.container.UnstrandedCacheContainter;

/**
 * 
 */
public abstract class AbstractDataAssemblerFactory {
	
	private final AbstractBuilderFactory builderFactory;
	
	public AbstractDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		this.builderFactory = builderFactory;
	}
	
	public abstract DataAssembler newInstance(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter,
			final int replicateI);
	
	protected AbstractBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
	protected CacheContainer createContainer(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {

		CacheContainer cacheContainer = null; 

		switch (conditionParameter.getLibraryType()) {

		case RF_FIRSTSTRAND: {
			final CacheContainer forwardCacheContainer = 
				new UnstrandedCacheContainter(
						sharedStorage, combineCaches(
								parameter, filterContainer, sharedStorage, conditionParameter) );
			final CacheContainer reverseCacheContainer = 
					new UnstrandedCacheContainter(
							sharedStorage, combineCaches(
									parameter, filterContainer, sharedStorage, conditionParameter) );

			cacheContainer = new RFPairedEnd1CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}

		case FR_SECONDSTRAND: {
			final CacheContainer forwardCacheContainer = 
				new UnstrandedCacheContainter(
						sharedStorage, 
						combineCaches(parameter, filterContainer, sharedStorage, conditionParameter) );
			final CacheContainer reverseCacheContainer = 
				new UnstrandedCacheContainter(
						sharedStorage, 
						combineCaches(parameter, filterContainer, sharedStorage, conditionParameter) );
			
			cacheContainer = new FRPairedEnd2CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}

		case UNSTRANDED: {
			cacheContainer = new UnstrandedCacheContainter(
					sharedStorage, 
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));
			break;
		}

		case MIXED:
			throw new IllegalArgumentException("Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}

		return cacheContainer;
	}

	protected abstract Cache createCache(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter);

	// combine data and filter cache
	private Cache combineCaches(
			final GeneralParameter parameter,
			final FilterContainer filterContainer, 
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {

		final Cache cache = new Cache();
		// create cache for data gathering 
		cache.addCache(createCache(parameter, sharedStorage, conditionParameter));
		// create cache for data filtering
		cache.addCache(filterContainer.createFilterCache(conditionParameter, sharedStorage));
		return cache;
	}
	
}
