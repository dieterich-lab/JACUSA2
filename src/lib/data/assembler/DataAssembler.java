package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.has.HasLibraryType;
import lib.util.coordinate.Coordinate;

public interface DataAssembler 
extends HasLibraryType {

	void buildCache(Coordinate activeWindowCoordinate, Iterator<SAMRecordWrapper> iterator);

	// Reset all caches in windows
	void clearCache();

	DataTypeContainer assembleData(Coordinate coordinate);

	CacheContainer getCacheContainer();

	ConditionParameter getConditionParameter();

	CACHE_STATUS getCacheStatus();

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};
	
}