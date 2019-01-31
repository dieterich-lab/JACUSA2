package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.has.HasLibraryType;
import lib.data.storage.container.CacheContainer;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;

public interface DataAssembler 
extends HasLibraryType {

	void buildCache(Coordinate activeWindowCoordinate, Iterator<SAMRecordExtended> iterator);

	// Reset storage in windows
	void clearStorage();

	DataContainer assembleData(Coordinate coordinate);

	CacheContainer getCacheContainer();

	ConditionParameter getConditionParameter();

	CACHE_STATUS getCacheStatus();

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};
	
}