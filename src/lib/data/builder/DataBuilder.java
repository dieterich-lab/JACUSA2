package lib.data.builder;

import jacusa.filter.FilterContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.Cache;
import lib.data.generator.DataGenerator;
import lib.data.has.hasLibraryType;
import lib.util.Coordinate;

public class DataBuilder<T extends AbstractData>
implements hasLibraryType {

	private final DataGenerator<T> dataGenerator;
	private final AbstractConditionParameter<T> conditionParameter;
	private final FilterContainer<T> filterContainer;

	private final LIBRARY_TYPE libraryType;
	
	private final List<Cache<T>> caches; 
	private CACHE_STATUS cacheStatus;

	public DataBuilder(
			final DataGenerator<T> dataGenerator, 
			final AbstractConditionParameter<T> conditionParameter,
			final LIBRARY_TYPE libraryType,
			List<Cache<T>> caches,
			FilterContainer<T> filterContainer) {
		
		this.dataGenerator = dataGenerator;
		this.conditionParameter	= conditionParameter;
		this.filterContainer = filterContainer;

		this.libraryType = libraryType;
		
		this.caches = caches;

		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public List<SAMRecordWrapper> buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<SAMRecordWrapper> iterator) {
		
		for (final Cache<T> cache : caches) {
			cache.clear();
			cache.setActiveWindowCoordinate(activeWindowCoordinate);
		}
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	
		final List<SAMRecordWrapper> recordWrappers = new ArrayList<SAMRecordWrapper>();

		while (iterator.hasNext()) {
			final SAMRecordWrapper recordWrapper = iterator.next();
			// process filters and decode
			recordWrapper.process();

			for (final Cache<T> cache : caches) {
				cache.addRecordWrapper(recordWrapper);
			}

			if (filterContainer != null) { // FIXME
				filterContainer.addRecordWrapper(recordWrapper);
			}
			recordWrappers.add(recordWrapper);
		}

		cacheStatus = recordWrappers.size() > 0 ? CACHE_STATUS.CACHED : CACHE_STATUS.NOT_FOUND; 
		return recordWrappers;
	}
	
	// Reset all caches in windows
	public void clearCache() {
		for (final Cache<T> cache : caches) {
			cache.clear();
		}
		filterContainer.clear();
	}

	public T getData(final Coordinate coordinate) {
		T data = dataGenerator.createData();
		for (final Cache<T> cache : caches) {
			cache.addData(data, coordinate);
		}
		data.setCoordinate(new Coordinate(coordinate));
		return data;
	}
	
	public List<Cache<T>> getCaches() {
		return caches;
	}
	
	public FilterContainer<T> getFilterContainer() {
		return filterContainer;
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
