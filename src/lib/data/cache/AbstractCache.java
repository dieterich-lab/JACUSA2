package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;

import lib.util.Coordinate;

public abstract class AbstractCache<T extends AbstractData> {

	private final int activeWindowSize;
	private Coordinate activeWindowCoordinate;
	
	public AbstractCache(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}
	
	public abstract void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	public abstract void clear();

	public abstract T getData(final Coordinate coordinate);
	
	/*
	protected abstract boolean isValid(final int windowPosition);
	
	// TODO
	public int getNext(int windowPosition) {
		while (isContainedInWindow(windowPosition)) {
			if (isValid(windowPosition)) {
				return windowPosition;
			}
			windowPosition++;
		}
		
		return -1;
	}
	*/

	public Coordinate getActiveWindowCoordinates() {
		return activeWindowCoordinate;
	}
	
	public void setWindowCoordinates(final Coordinate activeWindowCoordinate) {
		this.activeWindowCoordinate = activeWindowCoordinate;
		clear();
	}

	public int getActiveWindowSize() {
		return activeWindowSize;
	}

}
