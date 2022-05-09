package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractDataContainerBuilderFactory;
import lib.data.has.HasLibraryType;
import lib.data.storage.container.CacheContainer;
import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

/**
 * Defines an interface that will build a cache and use it to assemble Data into 
 * DataContainers.
 */
public interface DataAssembler extends HasLibraryType {
	
	/**
	 * Build cache for activeWindowCoordinate using reads from iterator
	 * @param activeWindowCoordinate to be used
	 * @param iterator to be used
	 */
	void buildCache(Coordinate activeWindowCoordinate, Iterator<ProcessedRecord> iterator);
	
	// 
	/**
	 * Reset storage in cache of "current" window
	 */
	void clearStorage();
	
	/**
	 * Create a DataContainer and populate it from window cache with data referenced by coordinate. 
	 * @param coordinate to use
	 * @return DataContainer with data from window cache 
	 */
	default DataContainer assembleData(Coordinate coordinate) {
		final DataContainer container = createDefaultDataContainer(coordinate);
		getCacheContainer().populate(container, coordinate);
		return container;
	}

	/**
	 * Create a default DataContainer.
	 * @param coordinate to be used
	 * @return default DataContainer
	 */
	default DataContainer createDefaultDataContainer(Coordinate coordinate) {
		final Base referenceBase = getCacheContainer().getReferenceProvider().getReferenceBase(coordinate);
		return getBuilderFactory().createBuilder(coordinate, getLibraryType())
				.withReferenceBase(referenceBase)
				.build();
	}
	
	AbstractDataContainerBuilderFactory getBuilderFactory();
	
	CacheContainer getCacheContainer();
	
	ConditionParameter getConditionParameter();
	
	CACHE_STATUS getCacheStatus();
	
	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};
	
}