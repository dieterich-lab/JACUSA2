package lib.record;

import java.io.IOException;

import lib.cli.parameter.ConditionParameter;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

/**
 * DOCUMENT
 */
public class RecordIteratorProvider {

	private final ConditionParameter conditionParameter;
	private SamReader reader;
	private final String fileName;

	private Coordinate currentCoordinate;
	private RecordIterator recordIterator;
	
	public RecordIteratorProvider(
			final ConditionParameter conditionParameter,
			final String fileName) {

		this.conditionParameter = conditionParameter;
		this.reader 			= ConditionParameter.createSamReader(fileName);
		this.fileName			= fileName;
	}

	public RecordIterator getIterator(final Coordinate activeWinCoord) {
		if (recordIterator == null) {
			currentCoordinate = activeWinCoord;
			final SAMRecordIterator samRecordIt = createSAMRecordIterator(activeWinCoord);
			recordIterator = new RecordIterator(conditionParameter, fileName, samRecordIt);
		} else if (! currentCoordinate.equals(activeWinCoord)) {
			currentCoordinate = activeWinCoord;
			final SAMRecordIterator samRecordIt = createSAMRecordIterator(activeWinCoord);
			recordIterator.updateIterator(samRecordIt);
		}

		return recordIterator;
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
	
	private SAMRecordIterator createSAMRecordIterator(final Coordinate winCoord) {
		check(winCoord);
		return reader.query(
				winCoord.getContig(), 
				winCoord.getStart(), 
				winCoord.getEnd(), 
				false);		
	}

	private void check(final Coordinate winCoord) {
		final int sequenceLength = reader
				.getFileHeader()
				.getSequence(winCoord.getContig())
				.getSequenceLength();
	
		if (winCoord.getEnd() > sequenceLength) {
			Coordinate samHeader = 
					new OneCoordinate(winCoord.getContig(), 1, sequenceLength - 1);
			AbstractTool.getLogger().addWarning(
					"Coordinates in BED file (" + winCoord.toString() + 
					") exceed SAM sequence header (" + samHeader.toString()+ ").");
		}
	}
		
}
