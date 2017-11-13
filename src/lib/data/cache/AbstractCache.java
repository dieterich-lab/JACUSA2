package lib.data.cache;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public abstract class AbstractCache<T extends AbstractData> 
implements Cache<T> {

	private final AbstractMethodFactory<T> methodFactory;
	private Coordinate activeWindowCoordinate;
	
	public AbstractCache(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
	}
	
	public AbstractMethodFactory<T> getDataGenerator() {
		return methodFactory;
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

	protected BaseCallConfig getBaseCallConfig() {
		return methodFactory.getParameter().getBaseConfig();
	}
	
	protected int getActiveWindowSize() {
		return methodFactory.getParameter().getActiveWindowSize();
	}
	
}
