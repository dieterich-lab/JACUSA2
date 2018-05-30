package lib.data.adder.basecall;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.VisitedReadPosition;

public class UniqueVisitBaseCallAdder<T extends AbstractData>
extends AbstractDataAdder<T> 
implements IncrementAdder<T>, VisitedReadPosition {

	private boolean[] visited;
	
	public UniqueVisitBaseCallAdder(final CoordinateController coordinateController) {
		super(coordinateController);
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		// nothing to be done
	}
	
	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		visited[readPosition] = true;
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
