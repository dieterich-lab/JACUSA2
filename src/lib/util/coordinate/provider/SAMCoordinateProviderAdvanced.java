/**
 * 
 */
package lib.util.coordinate.provider;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;

/**
 * @author mpiechotta
 *
 */
public class SAMCoordinateProviderAdvanced implements CoordinateProvider {

	private final boolean isStranded;
	private final List<SAMSequenceRecord> sequenceRecords;
	private final Iterator<SAMSequenceRecord> it;
	private SAMSequenceRecord sequenceRecord;
	
	private int reservedWindowSize;
	private final List<List<SamReader>> readers;

	private Coordinate current;
	
	/**
	 * 
	 */
	public SAMCoordinateProviderAdvanced(final boolean isStranded, final List<SAMSequenceRecord> sequenceRecords, 
			final AbstractParameter<?, ?> parameter) {

		this.isStranded = isStranded;
		this.sequenceRecords = sequenceRecords;
		it = sequenceRecords.iterator();
		
		this.reservedWindowSize = parameter.getReservedWindowSize();
		// create readers for conditions and replicate
		readers = new ArrayList<List<SamReader>>(parameter.getConditionsSize());
		for (final AbstractConditionParameter<?> condition : parameter.getConditionParameters()) {
			final List<SamReader> tmpReaders = new ArrayList<SamReader>(condition.getReplicateSize());
			for (String fileName : condition.getRecordFilenames()) {
				tmpReaders.add(AbstractConditionParameter.createSamReader(fileName));
			}
			readers.add(tmpReaders);
		}
		
		// init
		current = searchNext(null);
	}

	@Override
	public boolean hasNext() {
		return current != null;
	}

	@Override
	public Coordinate next() {
		if (hasNext()) {
			Coordinate tmp = current;
			current = searchNext(current);
			return tmp;
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		for (final List<SamReader> tmpList : readers) {
			for (final SamReader reader : tmpList) {
				reader.close();
			}	
		}
		readers.clear();
	}

	public int getTotal() {
		// TODO
		return sequenceRecords.size();
	}

	private Coordinate searchNext(Coordinate current) {
		int oldPosition;
		if (current == null) {
			if (! it.hasNext()) {
				return null;
			}
			sequenceRecord = it.next();
			oldPosition = 0;
		} else {
			oldPosition = current.getEnd();
		}
		
		int newPosition = 0;
		int found = 0;
		for (final List<SamReader> tmpList : readers) {
			for (final SamReader reader : tmpList) {
				final SAMRecordIterator it = reader.queryOverlapping(sequenceRecord.getSequenceName(), oldPosition + 1, 0);
				if (it.hasNext()) {
					final SAMRecord record = it.next();
					found++;
					int tmp = Math.max(oldPosition, record.getAlignmentStart());
					if (newPosition == 0) {
						newPosition = tmp;
					} else {
						newPosition = Math.min(tmp, tmp);
					}
				}
				it.close();
			}
		}
		if (found > 0) {
			final int end = Math.min(newPosition + reservedWindowSize, sequenceRecord.getSequenceLength());
			final Coordinate coordinate = new Coordinate(sequenceRecord.getSequenceName(), newPosition, end, isStranded ? STRAND.FORWARD : STRAND.UNKNOWN);
			return coordinate;
		}

		// advance to next sequenceRecord
		return searchNext(null);
	}

}