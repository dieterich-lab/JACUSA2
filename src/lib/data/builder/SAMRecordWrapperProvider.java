package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.util.AbstractTool;
import lib.util.Coordinate;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordWrapperProvider {

	private final SamReader reader;
	private final AbstractConditionParameter<?> condition;
	
	private int filteredSAMRecords;
	private int SAMRecords;

	public SAMRecordWrapperProvider (
			final SamReader reader, 
			final AbstractConditionParameter<?> condition) {
		this.reader			= reader;
		this.condition		= condition;
		
		filteredSAMRecords	= 0;
		SAMRecords			= 0;
	}

	// get iterator to fill the window
	public SAMRecordWrapperIterator getIterator(final Coordinate activeWindowCoordinate, final Coordinate reservedWindowCoordinate) {
		final SAMRecordIterator iterator = createSAMRecordIterator(reservedWindowCoordinate);
		return new SAMRecordWrapperIterator(this, activeWindowCoordinate, iterator);
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

	public int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}

	public int getSAMRecords() {
		return SAMRecords;
	}

	final public void incrementFilteredSAMRecords() {
		filteredSAMRecords++;
	}

	final public void incrementSAMRecords() {
		SAMRecords++;
	}
	
	public AbstractConditionParameter<?> getCondition() {
		return condition;
	}
		
}
