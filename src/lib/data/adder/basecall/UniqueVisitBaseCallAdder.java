package lib.data.adder.basecall;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.region.VisitedReadPosition;

public class UniqueVisitBaseCallAdder
extends AbstractDataContainerAdder 
implements IncrementAdder, VisitedReadPosition {

	private boolean[] visited;
	
	public UniqueVisitBaseCallAdder(final SharedCache sharedCache) {
		super(sharedCache);
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		// nothing to be done
	}
	
	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		visited[readPosition] = true;
	}

	@Override
	public int getCoverage(int windowPosition) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		// nothing to be done
	}

	@Override
	public boolean isVisited(int readPosition) {
		return visited[readPosition];
	}
	
	public void reset(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}
}
