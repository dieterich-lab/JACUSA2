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
	
	protected WindowPosition getWindowPosition(final Coordinate coordinate) {
		return getWindowPosition(coordinate.getPosition());
	}

	protected WindowPosition getWindowPosition(final int referencePosition) {
		final WindowPosition windowPosition = new WindowPosition();
		final Coordinate coordinate = getActiveWindowCoordinate();
		
		windowPosition.leftOffset = coordinate.getStart() - referencePosition;
		windowPosition.rightOffset = referencePosition - coordinate.getEnd();
		if (windowPosition.leftOffset < 0) {
			windowPosition.i = -1;
		} else if (windowPosition.rightOffset > 0) {
			windowPosition.i = -1;
		} else {
			windowPosition.i = windowPosition.leftOffset;
		}
		
		return windowPosition;
	}
	
	protected Entry<WindowPosition, STRAND> getStrandedWindowPosition(final Coordinate coordinate) {
		final WindowPosition windowPosition = getWindowPosition(coordinate);
		return new SimpleEntry<WindowPosition, STRAND>(windowPosition, coordinate.getStrand());
	}

	protected BaseCallConfig getBaseCallConfig() {
		return methodFactory.getParameter().getBaseConfig();
	}
	
	protected int getActiveWindowSize() {
		return methodFactory.getParameter().getActiveWindowSize();
	}
	
}
