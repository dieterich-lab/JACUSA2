package lib.data.builder.factory;

import java.util.List;

import jacusa.filter.FilterContainer;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.DataBuilder;
import lib.data.cache.Cache;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.FRPairedEnd1CacheContainer;
import lib.data.cache.container.FRPairedEnd2CacheContainer;
import lib.data.cache.container.UnstrandedCacheContainter;
import lib.data.generator.DataGenerator;
import lib.tmp.CoordinateController;

public abstract class AbstractDataBuilderFactory<T extends AbstractData> {
	
	private AbstractParameter<T, ?> generalParameter;

	public AbstractDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		this.generalParameter = generalParameter;
	}
	
	public DataBuilder<T> newInstance(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) throws IllegalArgumentException {
		
		final CacheContainer<T> cacheContainer = createContainer(coordinateController, conditionParameter);
		
		final DataGenerator<T> dataGenerator = getParameter().getMethodFactory().getDataGenerator();
		final FilterContainer<T> filterContainer = null; // TODO filter

		return new DataBuilder<T>(dataGenerator, conditionParameter, conditionParameter.getLibraryType(), 
				cacheContainer, filterContainer);
	}

	public CacheContainer<T> createContainer(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) {

		CacheContainer<T> cacheContainer = null; 

		switch (conditionParameter.getLibraryType()) {
		
		case FR_FIRSTSTRAND: {
			final CacheContainer<T> forwardCacheContainer = 
				new UnstrandedCacheContainter<T>(coordinateController, createCaches(coordinateController, conditionParameter));
			final CacheContainer<T> reverseCacheContainer = 
					new UnstrandedCacheContainter<T>(coordinateController, createCaches(coordinateController, conditionParameter));

			cacheContainer = new FRPairedEnd1CacheContainer<T>(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case FR_SECONDSTRAND: {
			final CacheContainer<T> forwardCacheContainer = 
				new UnstrandedCacheContainter<T>(coordinateController, createCaches(coordinateController, conditionParameter));
			final CacheContainer<T> reverseCacheContainer = 
				new UnstrandedCacheContainter<T>(coordinateController, createCaches(coordinateController, conditionParameter));
			
			cacheContainer = new FRPairedEnd2CacheContainer<T>(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case UNSTRANDED: {
			cacheContainer = new UnstrandedCacheContainter<T>(coordinateController, createCaches(coordinateController, conditionParameter));
			break;
		}
			
		case MIXED:
			throw new IllegalArgumentException("Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}
		
		return cacheContainer;
	}
	
	protected abstract List<Cache<T>> createCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter);
	
	public AbstractParameter<T, ?> getParameter() {
		return generalParameter;
	}
	
	public void setGeneralParameter(final AbstractParameter<T, ?> generalParameter)  {
		this.generalParameter = generalParameter;
	}
	
}
