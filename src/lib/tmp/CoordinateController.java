package lib.tmp;

import lib.util.Coordinate;
import lib.util.coordinateprovider.WindowedCoordinateProvider;

public class CoordinateController {

	private Coordinate reserved;
	private WindowedCoordinateProvider provider;
	private Coordinate active;
	
	public CoordinateController(final Coordinate reservedWindowCoordinate, int activeWindowSize) {}
	
	public void updateReserved(final Coordinate reservedWindowCoordinate, int activeWindowSize) {
		active = null;
		reserved = reservedWindowCoordinate;
		provider = new WindowedCoordinateProvider(reserved, activeWindowSize);
	}
	
	public boolean hasNext() {
		return provider.hasNext();
	}
	
	public Coordinate next() {
		active = provider.next(); 
		return active;
	}
	
	public Coordinate getActive() {
		return active;
	}
	
	public Coordinate getReserved() {
		return reserved;
	}

	public boolean isInner() {
		return ! (isLeft() || isRight());
	}
	
	public boolean isLeft() {
		return active.getStart() == reserved.getStart();
	}
	
	public boolean isRight() {
		return active.getEnd() == reserved.getEnd();
	}
	
}
