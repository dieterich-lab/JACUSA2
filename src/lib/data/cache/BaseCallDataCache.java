package lib.data.cache;

import htsjdk.samtools.AlignmentBlock;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.AbstractBaseCallRegionDataCache;
import lib.data.has.HasBaseCallCount;

public class BaseCallDataCache<T extends AbstractData & HasBaseCallCount>
extends AbstractBaseCallRegionDataCache<T> implements DataCache<T>{

	public BaseCallDataCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {
		super(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = alignmentBlock.getReferenceStart();
			final int readPosition = alignmentBlock.getReadStart() - 1;
			final int length = alignmentBlock.getLength();
			addRecordWrapperRegion(referencePosition, readPosition, length, recordWrapper);
		}
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), data.getBaseCallCount());
	}
	
}
