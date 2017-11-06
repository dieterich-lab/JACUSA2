package jacusa.util.coordinateprovider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jacusa.util.Coordinate;

public class WindowedCoordinateProvider implements CoordinateProvider {

	private final Coordinate reservedWindowCoordinate;
	private final int activeWindowSize;

	private int start;
	private int total;

	private Iterator<Coordinate> it;
	private List<Coordinate> coordinates;
	
	public WindowedCoordinateProvider(final Coordinate reservedWindowCoordinate, 
			final int activeWindowSize) {
		this.reservedWindowCoordinate = reservedWindowCoordinate; 
		this.activeWindowSize = activeWindowSize;
		start = reservedWindowCoordinate.getStart();
		
		coordinates = new ArrayList<Coordinate>();
		total = 0;
		while (_hasNext()) {
			coordinates.add(_next());
			total++;
		}
		it = coordinates.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}
	
	@Override
	public Coordinate next() {
		return it.next();
	}
	
	private boolean _hasNext() {
		return getNextActiveWindowCoordinate(reservedWindowCoordinate, start, activeWindowSize) != null;
	}
	
	private Coordinate _next() {
		if (! _hasNext()) {
			return null;
		}
	
		final Coordinate coordinate = new Coordinate(
				reservedWindowCoordinate.getContig(), 
				start, Math.min(reservedWindowCoordinate.getEnd(), start + activeWindowSize - 1));
		start += activeWindowSize;

		return coordinate;
	}
	
	private Coordinate getNextActiveWindowCoordinate(
			final Coordinate reservedWindowCoordinate, 
			final int start, 
			final int activeWindowSize) {
		if (! Coordinate.isContained(reservedWindowCoordinate, start + activeWindowSize - 1)) {
			return null;
		}

		return new Coordinate(
				reservedWindowCoordinate.getContig(), 
				start + activeWindowSize, 
				Math.min(reservedWindowCoordinate.getEnd(), start + activeWindowSize + activeWindowSize - 1));
	}

	public Coordinate getReservedWindowCoordinate() {
		return reservedWindowCoordinate;
	}

	@Override
	public void close() throws IOException {
		// nothing to be done here
	}
	
	public int getTotal() {
		return total;
	}

}