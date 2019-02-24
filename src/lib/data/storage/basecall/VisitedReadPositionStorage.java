package lib.data.storage.basecall;

import lib.recordextended.SAMRecordExtended;
import lib.data.DataContainer;
import lib.data.storage.Storage;
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

public class VisitedReadPositionStorage implements Storage {

	private final SharedStorage sharedStorage;
	
	private boolean[] visited;
	
	public VisitedReadPositionStorage(final SharedStorage sharedStorage) {
		this.sharedStorage = sharedStorage;
	}
	
	@Override
	public SharedStorage getSharedStorage() {
		return sharedStorage;
	}

	@Override
	public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
		// nothing to be done
	}
	
	@Override
	public void increment(Position pos) {
		visited[pos.getReadPosition()] = true;
	}
	
	@Override
	public void clear() {
		// nothing to be done
	}

	public boolean isVisited(int readPos) {
		return visited[readPos];			
	}
	
	public void reset(final SAMRecordExtended recordExtended) {
		visited = new boolean[recordExtended.getSAMRecord().getReadLength()];
	}
	
}
