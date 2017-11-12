package lib.data.builder;

import jacusa.filter.FilterContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.has.hasLibraryType;
import lib.util.Coordinate;

public abstract class AbstractDataBuilder<T extends AbstractData>
implements hasLibraryType {

	private final AbstractConditionParameter<T> conditionParameter;
	private final FilterContainer<T> filterContainer;

	private final LIBRARY_TYPE libraryType;
	
	private final Cache<T> cache; 
	private CACHE_STATUS cacheStatus;

	private Coordinate activeWindowCoordinate;

	public AbstractDataBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final LIBRARY_TYPE libraryType,
			final Cache<T> cache,
			FilterContainer<T> filterContainer) {
		this.conditionParameter	= conditionParameter;
		this.filterContainer = filterContainer;

		this.libraryType = libraryType;
		
		this.cache = cache;
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public List<SAMRecordWrapper> buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<SAMRecordWrapper> iterator) {
		
		cacheStatus	= CACHE_STATUS.NOT_CACHED;

		this.activeWindowCoordinate = activeWindowCoordinate;
		final List<SAMRecordWrapper> recordWrappers = new ArrayList<SAMRecordWrapper>();
		
		while (iterator.hasNext()) {
			final SAMRecordWrapper recordWrapper = iterator.next();
			// process filters and decode
			recordWrapper.process();
			cache.addRecordWrapper(recordWrapper);
			filterContainer.addRecordWrapper(recordWrapper);
			
			recordWrappers.add(recordWrapper);
		}

		cacheStatus = recordWrappers.size() > 0 ? CACHE_STATUS.CACHED : CACHE_STATUS.NOT_FOUND; 
		return recordWrappers;
	}
	
	// Reset all caches in windows
	public void clearCache() {
		cache.clear();
		filterContainer.clear();
	}

	public abstract T getData(final Coordinate coordinate);
	
	public Cache<T> getCache() {
		return cache;
	}
	
	public FilterContainer<T> getFilterContainer() {
		return filterContainer;
	}

	public Coordinate getActiveWindowCoordinate() {
		return activeWindowCoordinate;
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
