package lib.data.cache.lrtarrest;

import java.util.Map;

import lib.util.coordinate.CoordinateController;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public class LRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractLRTarrest2BaseCallCountDataCache<T> {

	public LRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(libraryType, minBASQ, baseCallConfig, coordinateController);
	}
	
}
