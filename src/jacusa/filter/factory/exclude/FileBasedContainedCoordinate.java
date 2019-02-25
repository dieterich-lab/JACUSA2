package jacusa.filter.factory.exclude;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.tribble.readers.SynchronousLineReader;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;
import lib.util.coordinate.OneCoordinate;

/**
 * This enables to query if specific coordinates are contained within a file or a pre-processed map. 
 * Files can be provided via implementing Codec(s) from htsjdk library.
 * No random access - successive calls to isContained must have ascending coordinates! 
 */
public class FileBasedContainedCoordinate implements ContainedCoordinate {

	// map of contig to successive coordinates
	private final Map<String, List<Coordinate>> contig2coordinate;

	private Coordinate current;
	private Iterator<Coordinate> it;
	
	public FileBasedContainedCoordinate(
			final String fileName, 
			FeatureCodec<? extends Feature, LineIterator> codec) {

		contig2coordinate = init(fileName, codec);
	}

	public FileBasedContainedCoordinate(final Map<String, List<Coordinate>> contig2coordinate) {
		this.contig2coordinate = contig2coordinate;
	}
	
	/**
	 * Tested in @see test.jacusa.filter.factory.exclude.DefaultContainedCoordinateTest
	 */
	@Override
	public boolean isContained(final Coordinate site) {
		// check if there is anything to filter against this site (contig)
		final String siteContig = site.getContig();
		if (! contig2coordinate.containsKey(siteContig)) {
			return false;
		}

		if (current == null || ! current.getContig().equals(siteContig)) {
			it = contig2coordinate.get(siteContig).iterator();
			if (! it.hasNext()) {
				return false;
			}
			current = it.next();
		}

		// manage it(erator) and current coordinate to try to find a match with site
		while (true) {
			final int orientation = CoordinateUtil.orientation(current, site);
			switch (orientation) {
			case -1:
				return false;

			case 0:
				return true;
				
			case 1:
				if (! it.hasNext()) {
					return false;
				}
				current = it.next();
				break;

			default:
				throw new IllegalStateException("Orientation can only be -1, 0, 1 but was: " + Integer.toString(orientation));
			}
		}
	}

	/**
	 * populate a Map from a file by using a codec
	 */
	public Map<String, List<Coordinate>> init(final String filename, 
			final FeatureCodec<? extends Feature, LineIterator> codec) {

		// container for results
		final Map<String, List<Coordinate>> contig2coordinate = new HashMap<String, List<Coordinate>>();

		// see documentation of codec in htsjdk
		LineIterator lit = null;
		try {
			final InputStream io 	= new FileInputStream(filename);
			final LineReader lr 	= new SynchronousLineReader(io);
			lit 					= new LineIteratorImpl(lr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Coordinate currentCoordinate = null;
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		while (lit.hasNext()) {
			try {
				final Feature f 		= codec.decode(lit);
				final String newContig 	= f.getContig();
				final int newStart		= f.getStart();
				final int newEnd		= f.getEnd();
				// feature returns one-based coords
				final Coordinate newCoordinate = new OneCoordinate(newContig, newStart, newEnd);

				if (currentCoordinate == null) { // init
					currentCoordinate = newCoordinate;
				} else if (! currentCoordinate.getContig().equals(newContig)) { // new contig
					if (coordinates.size() > 0) { // store list for old/current contig 
						contig2coordinate.put(currentCoordinate.getContig(), coordinates);
					}
					// reset
					coordinates = new ArrayList<Coordinate>();
					currentCoordinate = newCoordinate;
				} else if (currentCoordinate.getEnd() == newCoordinate.getStart()){ // extend existing
					// extend current coordinates
					CoordinateUtil.mergeCoordinate(currentCoordinate, newCoordinate);
				} else { // same contig but not adjacent coords 
					coordinates.add(currentCoordinate);
					currentCoordinate = newCoordinate;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// anything left?
		if (currentCoordinate != null) {
			coordinates.add(currentCoordinate);
		}

		if (! coordinates.isEmpty()) {
			contig2coordinate.put(currentCoordinate.getContig(), coordinates);
		}

		codec.close(lit);
		return contig2coordinate;
	}
	
}
