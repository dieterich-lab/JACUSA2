package lib.data.cache.region;

import htsjdk.samtools.SAMRecord;
import lib.data.AbstractData;
import lib.data.adder.basecall.UniqueVisitBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.isvalid.UniqueVisitBaseCallValidator;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public class UniqueTraverse<T extends AbstractData> 
implements RegionDataCache<T> {

	private final ValidatedRegionDataCache<T> dataCache;
	private SAMRecord record;

	private final UniqueVisitBaseCallAdder<T> adder;
	private final UniqueVisitBaseCallValidator validator;
	
	public UniqueTraverse(final ValidatedRegionDataCache<T> dataCache) {
		this.dataCache 	= dataCache;
		adder 			= new UniqueVisitBaseCallAdder<T>(dataCache.getCoordinateController());
		validator		= new UniqueVisitBaseCallValidator(adder);
		
		dataCache.addVIPAdder(adder);
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
	public void addData(T data, Coordinate coordinate) {
		dataCache.addData(data, coordinate);
	}

	@Override
	public CoordinateController getCoordinateController() {
		return dataCache.getCoordinateController();
	}

	@Override
	public void clear() {
		dataCache.clear();
	}
	
}
