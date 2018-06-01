package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.DataBuilder;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.FRPairedEnd1CacheContainer;
import lib.data.cache.container.FRPairedEnd2CacheContainer;
import lib.data.cache.container.UnstrandedCacheContainter;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.generator.DataGenerator;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractDataBuilderFactory<T extends AbstractData> {
	
	private AbstractParameter<T, ?> generalParameter;

	public AbstractDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		this.generalParameter = generalParameter;
	}
	
	public DataBuilder<T> newInstance(
			final ReferenceSetter<T> referenceSetter,
			final FilterContainer<T> filterContainer,
			final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter,
			final int replicateIndex) throws IllegalArgumentException {

		final CacheContainer<T> cacheContainer = createContainer(referenceSetter, filterContainer, coordinateController, conditionParameter, replicateIndex);
		
		final DataGenerator<T> dataGenerator = getParameter().getMethodFactory().getDataGenerator();

		return new DataBuilder<T>(replicateIndex, dataGenerator, conditionParameter, conditionParameter.getLibraryType(), 
				cacheContainer);
	}
	
	private CacheContainer<T> createContainer(
			final ReferenceSetter<T> referenceSetter,
			final FilterContainer<T> filterContainer,
			final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter,
			final int replicateIndex) {

		CacheContainer<T> cacheContainer = null; 
		
		switch (conditionParameter.getLibraryType()) {
		
		case FR_FIRSTSTRAND: {
			final CacheContainer<T> forwardCacheContainer = 
				new UnstrandedCacheContainter<T>(
						referenceSetter, coordinateController, createDataCaches(filterContainer, coordinateController, conditionParameter, replicateIndex));
			final CacheContainer<T> reverseCacheContainer = 
					new UnstrandedCacheContainter<T>(
							referenceSetter, coordinateController, createDataCaches(filterContainer, coordinateController, conditionParameter, replicateIndex));

			cacheContainer = new FRPairedEnd1CacheContainer<T>(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case FR_SECONDSTRAND: {
			final CacheContainer<T> forwardCacheContainer = 
				new UnstrandedCacheContainter<T>(
						referenceSetter, coordinateController, createDataCaches(filterContainer, coordinateController, conditionParameter, replicateIndex));
			final CacheContainer<T> reverseCacheContainer = 
				new UnstrandedCacheContainter<T>(
						referenceSetter, coordinateController, createDataCaches(filterContainer, coordinateController, conditionParameter, replicateIndex));
			
			cacheContainer = new FRPairedEnd2CacheContainer<T>(forwardCacheContainer, reverseCacheContainer);
			break;
		}
			
		case UNSTRANDED: {
			cacheContainer = new UnstrandedCacheContainter<T>(
					referenceSetter, coordinateController, createDataCaches(filterContainer, coordinateController, conditionParameter, replicateIndex));
			break;
		}
			
		case MIXED:
			throw new IllegalArgumentException("Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}
		
		return cacheContainer;
	}
	
	protected abstract List<RecordWrapperDataCache<T>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter);
	
	private List<RecordWrapperDataCache<T>> createDataCaches(
			final FilterContainer<T> filterContainer, 
			final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter,
			final int replicateIndex) {

		final List<RecordWrapperDataCache<T>> allCaches		= new ArrayList<RecordWrapperDataCache<T>>(3);
		final List<RecordWrapperDataCache<T>> dataCaches 	= createDataCaches(coordinateController, conditionParameter);
		final List<RecordWrapperDataCache<T>> filterCaches 	= filterContainer.createFilterCaches(conditionParameter, coordinateController);
		allCaches.addAll(dataCaches);
		allCaches.addAll(filterCaches);
		return allCaches;
	}
	
	public AbstractParameter<T, ?> getParameter() {
		return generalParameter;
	}
	
	public void setGeneralParameter(final AbstractParameter<T, ?> generalParameter)  {
		this.generalParameter = generalParameter;
	}
	
}
