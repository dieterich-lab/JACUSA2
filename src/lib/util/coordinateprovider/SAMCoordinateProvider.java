/**
 * 
 */
package lib.util.coordinateprovider;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import lib.util.Coordinate;

import htsjdk.samtools.SAMSequenceRecord;

/**
 * @author mpiechotta
 *
 */
public class SAMCoordinateProvider implements CoordinateProvider {

	private Iterator<SAMSequenceRecord> it;

	private int total;
	
	/**
	 * 
	 */
	public SAMCoordinateProvider(final List<SAMSequenceRecord> records) {
		it = records.iterator();
		total = records.size();
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Coordinate next() {
		if (hasNext()) {
			final SAMSequenceRecord record = it.next();
			return new Coordinate(record.getSequenceName(), 1, record.getSequenceLength());
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		// not needed
	}

	public int getTotal() {
		return total;
	}
	
}