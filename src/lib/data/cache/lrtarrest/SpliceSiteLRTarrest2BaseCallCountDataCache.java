package lib.data.cache.lrtarrest;

import java.util.Map;

import lib.util.coordinate.CoordinateController;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.data.has.filter.HasLRTarrestSpliceSiteFilteredData;

public class SpliceSiteLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasLRTarrestSpliceSiteFilteredData> 
extends AbstractUniqueLRTarrest2BaseCallCountDataCache<T> {

		public SpliceSiteLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(libraryType, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	protected void addRefPos2bc(final Map<Integer, BaseCallCount> ref2bc, final T data) {
		data.setLRTarrestSpliceSiteFilteredData(ref2bc);
	}

	
}
