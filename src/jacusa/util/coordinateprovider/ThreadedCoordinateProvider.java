package jacusa.util.coordinateprovider;

import jacusa.util.Coordinate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

public class ThreadedCoordinateProvider implements CoordinateProvider {

	private CoordinateProvider cp;
	private int reservedWindowSize;

	private Coordinate buffer;
	private Coordinate current;

	private SAMFileReader[] readers;
	
	private List<Coordinate> coordinates;
	private Iterator<Coordinate> it;
	private int total;
	
	public ThreadedCoordinateProvider(final CoordinateProvider cp, final String[][] pathnames, final int reservedWindowSize) {
		this.cp = cp;
		this.reservedWindowSize = reservedWindowSize;

		int totalReplicates = 0;
		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			totalReplicates += pathnames[conditionIndex].length;
		}

		readers = new SAMFileReader[totalReplicates];
		int globalReplicates = 0;
		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			System.arraycopy(
					initReaders(pathnames[conditionIndex]), 
					0, 
					readers, 
					globalReplicates, 
					pathnames[conditionIndex].length);
			globalReplicates += pathnames[conditionIndex].length;
		}
		
		coordinates = new ArrayList<Coordinate>(cp.getTotal());
		total = 0;
		while (_hasNext()) {
			coordinates.add(_next());
			total++;
		}
		it = coordinates.iterator();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public Coordinate next() {
		return it.next();
	}
	
	private boolean _hasNext() {
		if (current != null) {
			return true;
		}

		if (buffer == null && cp.hasNext()) {
			buffer = cp.next();
		}
		if (buffer != null) {
			current = advance(buffer);
			if (current == null) {
				buffer = null;
				return _hasNext();
			}
			return true;
		}
		
		return false;
	}
	
	private Coordinate _next() {
		Coordinate tmp = null;
		if (_hasNext()) {
			tmp = new Coordinate(current);
			current = null;
		}
	
		return tmp;
	}

	@Override
	public void close() throws IOException {
		cp.close();
	}

	private Coordinate advance(final Coordinate coordinate) {
		if (coordinate.getStart() > coordinate.getEnd()) {
			return null; 
		}
		final Coordinate tmp = new Coordinate(coordinate);

		int start = tmp.getStart();
		for (final SAMFileReader reader : readers) {
			final SAMRecordIterator iterator = reader.query(coordinate.getContig(), start, coordinate.getEnd(), false);

			boolean found = false;
			while (iterator.hasNext()) {
				final SAMRecord record = iterator.next();
				if (! record.getReadUnmappedFlag() && ! record.getNotPrimaryAlignmentFlag()) {
					start = Math.max(start, record.getAlignmentStart());
					found = true;
					break;
				}
			}
			iterator.close();
			if (! found) {
				return null;
			}
		}

		final int end = Math.min(start + reservedWindowSize - 1, coordinate.getEnd());
		tmp.setStart(start);
		tmp.setEnd(end);

		coordinate.setStart(end + 1);
		return tmp;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 */
	protected SAMFileReader[] initReaders(final String[] pathnames) {
		final SAMFileReader[] readers = new SAMFileReader[pathnames.length];
		for(int i = 0; i < pathnames.length; ++i) {
			readers[i] = initReader(pathnames[i]);
		}
		return readers;
	}

	/**
	 * 
	 * @param pathname
	 * @return
	 */
	protected SAMFileReader initReader(final String pathname) {
		SAMFileReader reader = new SAMFileReader(new File(pathname));
		// be silent
		reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
		// disable memory mapping
		reader.enableIndexCaching(true);
		reader.enableIndexMemoryMapping(false);
		return reader;
	}

	public int getTotal() {
		return total;
	}

}
