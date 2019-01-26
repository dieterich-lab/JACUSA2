package lib.data.cache.region;

import htsjdk.samtools.SAMRecord;
import lib.data.DataTypeContainer;
import lib.data.adder.basecall.UniqueVisitBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.region.isvalid.UniqueVisitBaseCallValidator;
import lib.util.coordinate.Coordinate;

public class UniqueTraverse implements RegionDataCache {

	private final ValidatedRegionDataCache dataCache;
	private SAMRecord record;

	private final UniqueVisitBaseCallAdder adder;
	private final UniqueVisitBaseCallValidator validator;
	
	public UniqueTraverse(final ValidatedRegionDataCache dataCache) {
		this.dataCache 	= dataCache;
		adder 			= new UniqueVisitBaseCallAdder(dataCache.getShareCache());
		validator		= new UniqueVisitBaseCallValidator(adder);
		
		dataCache.addHeadAdder(adder);
		dataCache.addValidator(validator);
	}

	@Override
	public void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper) {
		if (record == null || recordWrapper.getSAMRecord() != record) {
			adder.reset(recordWrapper);
		}
		dataCache.addRegion(referencePosition, readPosition, length, recordWrapper);
	}

	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		dataCache.populate(container, coordinate);
	}

	@Override
	public void clear() {
		dataCache.clear();
	}
	
	@Override
	public SharedCache getShareCache() {
		return dataCache.getShareCache();
	}
	
}
