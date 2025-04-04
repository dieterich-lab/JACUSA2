package lib.util.coordinate.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.options.ThreadWindowSizeOption;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;

/**
 * DOCUMENT
 */
public class WindowedCoordinateStaticProvider implements CoordinateProvider {

	private Iterator<Coordinate> it;
	private int total;
	
	public WindowedCoordinateStaticProvider(final boolean stranded, final Coordinate coordinate, final int windowSize) {
		final List<Coordinate> coordinates = makeWindows(stranded, coordinate, windowSize);
		total = coordinates.size();
		it = coordinates.iterator();
	}
	
	public WindowedCoordinateStaticProvider(final boolean stranded, final CoordinateProvider cp, final int windowSize) {
		final List<Coordinate> coordinates = makeWindows(stranded, cp, windowSize);
		total = coordinates.size();
		it = coordinates.iterator();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public Coordinate next() {
		return it.next();
	}
	
	@Override
	public void close() throws IOException {
		// not needed
	}

	public int getTotal() {
		return total;
	}

	private List<Coordinate> makeWindows(final boolean stranded, final CoordinateProvider cp, final int windowSize) {
		final List<Coordinate> coordinates = new ArrayList<Coordinate>(cp.getTotal());
		while (cp.hasNext()) {
			final Coordinate coordinate = cp.next();
			final List<Coordinate> windowedCoordinates = makeWindows(stranded, coordinate, windowSize);
			if (! windowedCoordinates.isEmpty()) {
				coordinates.addAll(windowedCoordinates);
			}
		}
		return coordinates;
	}
	
	private List<Coordinate> makeWindows(final boolean stranded, final Coordinate coordinate, final int windowSize) {
		if (windowSize == ThreadWindowSizeOption.NO_WINDOWS) {
			final List<Coordinate> coordinates = new ArrayList<>(1);
			final Coordinate tmp = coordinate.copy();
			if (stranded && coordinate.getStrand() == STRAND.UNKNOWN) {
				tmp.setStrand(STRAND.FORWARD);
			}
			coordinates.add(tmp);
			return coordinates;
		}
		
		final int length 	= coordinate.get1End() - coordinate.get1Start() + 1;
		final int n 		= length / windowSize;
		final List<Coordinate> coordinates = new ArrayList<>(n < 0 ? 1 : n);
		
		int start 	= coordinate.getStart();
		int end 	= Math.min(start + windowSize - 1, coordinate.getEnd());
		while (end <= coordinate.getEnd() && start <= end) {
			final Coordinate tmp = new OneCoordinate(
					coordinate.getContig(), 
					start, end, 
					coordinate.getStrand());
			if (stranded && coordinate.getStrand() == STRAND.UNKNOWN) {
				tmp.setStrand(STRAND.FORWARD);
			}
			coordinates.add(tmp);

			start	= end + 1;
			end 	= Math.min(start + windowSize - 1, coordinate.getEnd());
		}

		return coordinates;
	}
	
}
