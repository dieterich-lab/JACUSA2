/**
 * 
 */
package lib.util.coordinate.provider;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.Coordinate.STRAND;

import htsjdk.samtools.SAMSequenceRecord;

/**
 * @author mpiechotta
 *
 */
public class SAMCoordinateProvider implements CoordinateProvider {

	final boolean isStranded;
	private Iterator<SAMSequenceRecord> it;
	private int total;
	
	/**
	 * 
	 */
	public SAMCoordinateProvider(final boolean isStranded, final List<SAMSequenceRecord> records) {
		this.isStranded = isStranded;
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
			return new Coordinate(record.getSequenceName(), 1, record.getSequenceLength(), isStranded ? STRAND.FORWARD : STRAND.UNKNOWN);
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