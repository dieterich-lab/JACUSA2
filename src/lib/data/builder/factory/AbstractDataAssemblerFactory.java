package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.RFPairedEnd1CacheContainer;
import lib.data.cache.container.FRPairedEnd2CacheContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.container.UnstrandedCacheContainter;
import lib.data.cache.record.RecordWrapperProcessor;

public abstract class AbstractDataAssemblerFactory {
	
	private final AbstractBuilderFactory builderFactory;
	
	public AbstractDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		this.builderFactory = builderFactory;
	}
	
	public abstract DataAssembler newInstance(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedCache sharedCache, 
			final ConditionParameter conditionParameter,
			final int replicateIndex) throws IllegalArgumentException;
	
	protected AbstractBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
	protected CacheContainer createContainer(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedCache sharedCache, 
			final ConditionParameter conditionParameter,
			final int replicateIndex) {

		CacheContainer cacheContainer = null; 
		
		switch (conditionParameter.getLibraryType()) {
		
		case RF_FIRSTSTRAND: {
			final CacheContainer forwardCacheContainer = 
				new UnstrandedCacheContainter(
						sharedCache, createCaches(
								parameter, filterContainer, sharedCache, conditionParameter) );
			final CacheContainer reverseCacheContainer = 
					new UnstrandedCacheContainter(
							sharedCache, createCaches(
									parameter, filterContainer, sharedCache, conditionParameter) );

			cacheContainer = new RFPairedEnd1CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case FR_SECONDSTRAND: {
			final CacheContainer forwardCacheContainer = 
				new UnstrandedCacheContainter(
						sharedCache, 
						createCaches(parameter, filterContainer, sharedCache, conditionParameter) );
			final CacheContainer reverseCacheContainer = 
				new UnstrandedCacheContainter(
						sharedCache, 
						createCaches(parameter, filterContainer, sharedCache, conditionParameter) );
			
			cacheContainer = new FRPairedEnd2CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case UNSTRANDED: {
			cacheContainer = new UnstrandedCacheContainter(
					sharedCache, 
					createCaches(parameter, filterContainer, sharedCache, conditionParameter));
			break;
		}
			
		case MIXED:
			throw new IllegalArgumentException("Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}
		
		return cacheContainer;
	}
	
	protected abstract List<RecordWrapperProcessor> createCaches(
			final GeneralParameter parameter,
			final SharedCache sharedCache, 
			final ConditionParameter conditionParameter);
	
	private List<RecordWrapperProcessor> createCaches(
			final GeneralParameter parameter,
			final FilterContainer filterContainer, 
			final SharedCache sharedCache, 
			final ConditionParameter conditionParameter) {

		final List<RecordWrapperProcessor> allCaches	= new ArrayList<RecordWrapperProcessor>(6);
		final List<RecordWrapperProcessor> dataCaches 	= createCaches(parameter, sharedCache, conditionParameter);
		final List<RecordWrapperProcessor> filterCaches = filterContainer.createFilterCaches(conditionParameter, sharedCache);
		allCaches.addAll(dataCaches);
		allCaches.addAll(filterCaches);
		return allCaches;
	}
	
}
