package lib.data.adder.basecall;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.cache.arrest.LocationInterpreter;
import lib.data.cache.container.SharedCache;

public class RTarrestBaseCallAdder
extends AbstractDataContainerAdder 
implements IncrementAdder {

	private final LocationInterpreter locInterpreter;
	
	private final IncrementAdder arrest;
	private final IncrementAdder through;
	
	public RTarrestBaseCallAdder(
			final SharedCache sharedCache,
			final LocationInterpreter locationInterpreter,
			final IncrementAdder arrest,
			final IncrementAdder through) {

		super(sharedCache);
		locInterpreter = locationInterpreter;
		this.arrest = arrest;
		this.through = through;
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		arrest.populate(container, coordinate);
		through.populate(container, coordinate);
	}
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		if (locInterpreter.isArrest(record, readPosition)) {
			arrest.increment(referencePosition, windowPosition, readPosition, base, baseQuality, record);
		} else {
			through.increment(referencePosition, windowPosition, readPosition, base, baseQuality, record);
		}
	}
	
	@Override
	public void clear() {
		arrest.clear();
		through.clear();
	}

	@Override
	public int getCoverage(final int windowPosition) {
		return arrest.getCoverage(windowPosition) + through.getCoverage(windowPosition);
	}

}
