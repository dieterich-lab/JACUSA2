package lib.data.cache.arrest;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.Region;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperDataCache;

public class RTarrestDataCache 
extends AbstractDataContainerAdder 
implements RecordWrapperDataCache {

	private final LocationInterpreter locInterpreter;
	
	private final ValidatedRegionDataCache arrest;
	private final ValidatedRegionDataCache through;

	public RTarrestDataCache(
			final LocationInterpreter locactionInterpreter,
			final ValidatedRegionDataCache arrest,
			final ValidatedRegionDataCache through,
			final SharedCache sharedCache) {
		
		super(sharedCache);
		
		locInterpreter = locactionInterpreter;
		this.arrest = arrest;
		this.through = through;
	}
	
	@Override
	public void processRecordWrapper(SAMRecordWrapper recordWrapper) {
		for (final Region region : locInterpreter.getArrestRegions(recordWrapper.getSAMRecord())) {
			arrest.addRegion(
					region.getReferenceStart(), 
					region.getReadStart(), 
					region.getLength(), 
					recordWrapper);
		}
		for (final Region region : locInterpreter.getThroughRegions(recordWrapper.getSAMRecord())) {
			through.addRegion(
					region.getReferenceStart(), 
					region.getReadStart(), 
					region.getLength(), 
					recordWrapper);
		}
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		// store arrest base calls
		arrest.populate(container, coordinate);
		through.populate(container, coordinate);
	}

	@Override
	public void clear() {
		arrest.clear();
		through.clear();
	}
	
}
