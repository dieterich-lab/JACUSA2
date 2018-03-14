package lib.data.cache;

import lib.util.coordinate.CoordinateController;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

/**
 * TODO add comments. Makes sure that each position is only counted once
 * 
 * @param <T>
 */
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

	public abstract void addRecordWrapperRegion(int readPosition, int length, 
			SAMRecordWrapper recordWrapper);

}
