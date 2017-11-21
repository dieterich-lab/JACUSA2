package lib.data.cache.container;

import java.io.IOException;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;

import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class SAMReaderNextPositionCache implements NextPositionCache {

	private SamReader samReader;
	private final CoordinateController coordinateController;

	public SAMReaderNextPositionCache(final String filename, final CoordinateController coordinateController) {
		samReader = AbstractConditionParameter.createSamReader(filename);
		this.coordinateController = coordinateController;
		
		clear();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		final Coordinate coordinate = coordinateController.getCoordinateAdvancer().getCurrentCoordinate();
		final String sequence = coordinate.getContig();
		final int start  = coordinate.getStart();
		final int end = coordinate.getEnd();

		final int referencePosition = start + windowPosition + 1;

		final SAMRecordIterator iterator = samReader.queryOverlapping(sequence, referencePosition, end);
		if (! iterator.hasNext()) {
			final SAMRecord record = iterator.next();
			iterator.close();
			return coordinateController.convert2windowPosition(
					Math.max(referencePosition, record.getAlignmentStart()));
		}

		iterator.close();
		return -1;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
	}

	@Override
	public void clear() {
	}
	
	public void close() {
		try {
			samReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
