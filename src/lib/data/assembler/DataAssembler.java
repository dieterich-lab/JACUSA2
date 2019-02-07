package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.has.HasLibraryType;
import lib.data.storage.container.CacheContainer;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;

public interface DataAssembler 
extends HasLibraryType {

	void buildCache(Coordinate activeWindowCoordinate, Iterator<SAMRecordExtended> iterator);

	// Reset storage in windows
	void clearStorage();

	default DataContainer assembleData(Coordinate coordinate) {
		final DataContainer container = createDefaultDataContainer(coordinate);
		getCacheContainer().populate(container, coordinate);
		return container;
	}
	
	default DataContainer createDefaultDataContainer(Coordinate coordinate) {
		final Base referenceBase = getCacheContainer().getReferenceProvider().getReferenceBase(coordinate);
		return getBuilderFactory().createBuilder(coordinate, getLibraryType())
				.withReferenceBase(referenceBase)
				.build();
	}
	
	AbstractBuilderFactory getBuilderFactory();
	
	CacheContainer getCacheContainer();

	ConditionParameter getConditionParameter();

	CACHE_STATUS getCacheStatus();

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};
	
}