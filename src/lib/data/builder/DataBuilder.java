package lib.data.builder;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.generator.DataGenerator;
import lib.data.has.hasLibraryType;
import lib.util.coordinate.Coordinate;

public class DataBuilder<T extends AbstractData>
implements hasLibraryType {

	private final DataGenerator<T> dataGenerator;
	private final AbstractConditionParameter<T> conditionParameter;
	private final List<FilterCache<?>> filterCaches;

	private final LIBRARY_TYPE libraryType;
	
	private final CacheContainer<T> cacheContainer; 
	private CACHE_STATUS cacheStatus;

	public DataBuilder(
			final DataGenerator<T> dataGenerator, 
			final AbstractConditionParameter<T> conditionParameter,
			final LIBRARY_TYPE libraryType,
			final CacheContainer<T> cacheContainer,
			final List<FilterCache<?>> filterCaches) {
		
		this.dataGenerator = dataGenerator;
		this.conditionParameter	= conditionParameter;
		this.filterCaches = filterCaches;

		this.libraryType = libraryType;
		
		this.cacheContainer = cacheContainer;

		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public List<SAMRecordWrapper> buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<SAMRecordWrapper> iterator) {
		
		cacheContainer.clear();
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	
		final List<SAMRecordWrapper> recordWrappers = new ArrayList<SAMRecordWrapper>();

		while (iterator.hasNext()) {
			final SAMRecordWrapper recordWrapper = iterator.next();
			// process filters and decode
			recordWrapper.process();
			cacheContainer.addRecordWrapper(recordWrapper);
			for (FilterCache<?> filterCache : filterCaches) {
				filterCache.addRecordWrapper(recordWrapper);
			}
			recordWrappers.add(recordWrapper);
		}

		cacheStatus = recordWrappers.size() > 0 ? CACHE_STATUS.CACHED : CACHE_STATUS.NOT_FOUND; 
		return recordWrappers;
	}
	
	// Reset all caches in windows
	public void clearCache() {
		cacheContainer.clear();
		for (FilterCache<?> filterCache : filterCaches) {
			filterCache.clear();
		}
	}

	public T getData(final Coordinate coordinate) {
		T data = dataGenerator.createData(getLibraryType(), coordinate);
		cacheContainer.addData(data, coordinate);
		return data;
	}
	
	public CacheContainer<T> getCacheContainer() {
		return cacheContainer;
	}

	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameter;
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};

}
