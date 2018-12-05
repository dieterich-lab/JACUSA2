package lib.data.builder.recordwrapper;

import java.io.IOException;

import lib.cli.parameter.ConditionParameter;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordWrapperIteratorProvider {

	private ConditionParameter conditionParameter;
	private SamReader reader;

	private Coordinate current;
	private SAMRecordWrapperIterator recordWrapperIterator;
	
	public SAMRecordWrapperIteratorProvider(
			final ConditionParameter conditionParameter,
			final SamReader reader) {

		this.conditionParameter = conditionParameter;
		this.reader = reader;
	}

	public SAMRecordWrapperIterator getIterator(final Coordinate activeWindowCoordinate) {
		if (recordWrapperIterator == null) {
			current = activeWindowCoordinate;
			final SAMRecordIterator recordIterator = createSAMRecordIterator(activeWindowCoordinate);
			recordWrapperIterator = new SAMRecordWrapperIterator(conditionParameter, recordIterator);
		} else if (! current.equals(activeWindowCoordinate)) {
			current = activeWindowCoordinate;
			final SAMRecordIterator recordIterator = createSAMRecordIterator(activeWindowCoordinate);
			recordWrapperIterator.updateIterator(recordIterator);
		}

		return recordWrapperIterator;
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
			Coordinate samHeader = new Coordinate(windowCoordinate.getContig(), 1, sequenceLength);
			AbstractTool.getLogger().addWarning("Coordinates in BED file (" + windowCoordinate.toString() + 
					") exceed SAM sequence header (" + samHeader.toString()+ ").");
		}
	}
		
}
