package lib.record;

import java.io.IOException;

import lib.cli.parameter.ConditionParameter;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecordIterator;

/**
 * TODO
 */
public class RecordIteratorProvider {

	private final ConditionParameter condPrm;
	private SamReader reader;
	private final String fileName;
	private byte enforceBASQ;

	private Coordinate curCord;
	private RecordIterator recordIt;

	public RecordIteratorProvider(
			final ConditionParameter condPrm,
			final String fileName) {
		this(condPrm, fileName, (byte)-1);
	}
	
	public RecordIteratorProvider(
			final ConditionParameter condPrm,
			final String fileName, 
			final byte enforceBASQ) {

		this.condPrm 		= condPrm;
		this.reader 		= ConditionParameter.createSamReader(fileName);
		this.fileName		= fileName;
		this.enforceBASQ 	= enforceBASQ;
	}

	private RecordIterator createRecordIterator(final SAMRecordIterator samRecordIt) {
		if (enforceBASQ >= 0) {
			return new EnforceBASQRecordIterator(condPrm, fileName, enforceBASQ, samRecordIt);
		}
		
		return new DefaultRecordIterator(condPrm, fileName, samRecordIt);
	}
	
	public RecordIterator getIterator(final Coordinate activeWinCoord) {
		if (recordIt == null) {
			curCord = activeWinCoord;
			final SAMRecordIterator samRecordIt = createSAMRecordIterator(activeWinCoord);
			recordIt = createRecordIterator(samRecordIt);
		} else if (! curCord.equals(activeWinCoord)) {
			curCord = activeWinCoord;
			final SAMRecordIterator samRecordIt = createSAMRecordIterator(activeWinCoord);
			recordIt.updateIterator(samRecordIt);
		}

		return recordIt;
	}

	public ConditionParameter getConditionParameter() {
		return condPrm;
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
