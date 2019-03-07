package lib.recordextended;

import java.io.IOException;

import lib.cli.parameter.ConditionParameter;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordExtendedIteratorProvider {

	private final ConditionParameter conditionParameter;
	private SamReader reader;
	private final String fileName;

	private Coordinate current;
	private SAMRecordExtendedIterator recordExtendedIterator;
	
	public SAMRecordExtendedIteratorProvider(
			final ConditionParameter conditionParameter,
			final String fileName) {

		this.conditionParameter = conditionParameter;
		this.reader 			= ConditionParameter.createSamReader(fileName);
		this.fileName			= fileName;
	}

	public SAMRecordExtendedIterator getIterator(final Coordinate activeWindowCoordinate) {
		if (recordExtendedIterator == null) {
			current = activeWindowCoordinate;
			final SAMRecordIterator recordIterator = createSAMRecordIterator(activeWindowCoordinate);
			recordExtendedIterator = new SAMRecordExtendedIterator(conditionParameter, fileName, recordIterator);
		} else if (! current.equals(activeWindowCoordinate)) {
			current = activeWindowCoordinate;
			final SAMRecordIterator recordIterator = createSAMRecordIterator(activeWindowCoordinate);
			recordExtendedIterator.updateIterator(recordIterator);
		}

		return recordExtendedIterator;
	}

	public ConditionParameter getConditionParameter() {
		return conditionParameter;
	}

	public void close() {
		try {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private SAMRecordIterator createSAMRecordIterator(final Coordinate windowCoordinate) {
		check(windowCoordinate);
		return reader.query(
				windowCoordinate.getContig(), 
				windowCoordinate.getStart(), 
				windowCoordinate.getEnd(), 
				false);		
	}

	private void check(final Coordinate windowCoordinate) {
		final int sequenceLength = reader
				.getFileHeader()
				.getSequence(windowCoordinate.getContig())
				.getSequenceLength();
	
		if (windowCoordinate.getEnd() > sequenceLength) {
			Coordinate samHeader = 
					new OneCoordinate(windowCoordinate.getContig(), 1, sequenceLength - 1);
			AbstractTool.getLogger().addWarning(
					"Coordinates in BED file (" + windowCoordinate.toString() + 
					") exceed SAM sequence header (" + samHeader.toString()+ ").");
		}
	}
		
}
