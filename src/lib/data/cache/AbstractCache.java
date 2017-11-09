package lib.data.cache;

import lib.data.builder.SAMRecordWrapper;

import lib.util.Coordinate;

public abstract class AbstractCache {

	private final int activeWindowSize;
	private Coordinate activeWindowCoordinate;
	
	public AbstractCache(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}
	
	public abstract void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	public abstract void clear();
	
	/*
	protected abstract boolean isValid(final int windowPosition);
	
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

	// TODO
	private boolean isContainedInWindow(final int windowPosition) {
		return windowPosition < activeWindowSize;
	}

	public Coordinate getActiveWindowCoordinates() {
		return activeWindowCoordinate;
	}
	
	public void setWindowCoordinates(final Coordinate activeWindowCoordinate) {
		if (activeWindowCoordinate.getEnd() - activeWindowCoordinate.getStart() + 1 != getActiveWindowSize()) {
			throw new IllegalArgumentException();
		}
		this.activeWindowCoordinate = activeWindowCoordinate;
		clear();
	}

	public int getActiveWindowSize() {
		return activeWindowSize;
	}

}
