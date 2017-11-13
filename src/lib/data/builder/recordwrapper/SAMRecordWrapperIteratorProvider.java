package lib.data.builder.recordwrapper;

import java.io.IOException;

import lib.cli.parameters.AbstractConditionParameter;
import lib.util.AbstractTool;
import lib.util.Coordinate;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordWrapperIteratorProvider {

	private AbstractConditionParameter<?> conditionParameter;
	private final SamReader reader;
	
	private int acceptedSAMRecords;
	private int filteredSAMRecords;
	
	public SAMRecordWrapperIteratorProvider(final AbstractConditionParameter<?> conditionParameter,
			final SamReader reader) {
		this.conditionParameter = conditionParameter;
		this.reader = reader;
		
		acceptedSAMRecords = 0;
		filteredSAMRecords = 0;
	}

	/*
	// get iterator to fill the window
	public SAMRecordWrapperIterator createIterator(final Coordinate activeWindowCoordinate, final Coordinate reservedWindowCoordinate) {
		final SAMRecordIterator iterator = createSAMRecordIterator(reservedWindowCoordinate);
		return new SAMRecordWrapperIterator(this, activeWindowCoordinate, iterator);
	}
	*/
	// FIXME
	public SAMRecordWrapperIterator createIterator(final Coordinate activeWindowCoordinate) {
		final SAMRecordIterator iterator = createSAMRecordIterator(activeWindowCoordinate);
		return new SAMRecordWrapperIterator(this, iterator);
	}
	
	public final void incrementAcceptedSAMRecords() {
		acceptedSAMRecords++;
	}

	public final void incrementFilteredSAMRecords() {
		filteredSAMRecords++;
	}
	
	public final int getAcceptedSAMRecords() {
		return acceptedSAMRecords;
	}
	
	public final int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}

	public AbstractConditionParameter<?> getConditionParameter() {
		return conditionParameter;
	}
	
	public void close() {
		try {
			reader.close();
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
