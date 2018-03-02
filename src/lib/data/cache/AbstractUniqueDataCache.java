package lib.data.cache;

import lib.util.coordinate.CoordinateController;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public abstract class AbstractUniqueDataCache<T extends AbstractData> 
extends AbstractDataCache<T> {
	
	private boolean[] visited;
	
	public AbstractUniqueDataCache(final CoordinateController coordinateController) {
		super(coordinateController);
	}

	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}

	protected boolean[] getVisited() {
		return visited;
	}

	public abstract void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper);
	
}
