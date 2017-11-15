package lib.data.cache;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lib.data.AbstractData;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.Coordinate.STRAND;

public abstract class AbstractCache<T extends AbstractData> 
implements Cache<T> {

	private int activeWindowSize;
	private Coordinate activeWindowCoordinate;
	
	public AbstractCache(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}
	
	public void setActiveWindowCoordinate(final Coordinate activeWindowCoordinate) {
		this.activeWindowCoordinate = activeWindowCoordinate;
		clear();
	}
	
	public Coordinate getActiveWindowCoordinate() {
		return activeWindowCoordinate;
	}
	
	protected int getWindowPosition(final Coordinate coordinate) {
		return Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
	}
	
	protected Entry<Integer, STRAND> getStrandedWindowPosition(final Coordinate coordinate) {
		final int windowPosition = getWindowPosition(coordinate);
		return new SimpleEntry<Integer, STRAND>(windowPosition, coordinate.getStrand());
	}

	protected int getActiveWindowSize() {
		return activeWindowSize;
	}
	
}
