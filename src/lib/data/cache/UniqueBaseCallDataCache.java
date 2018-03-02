package lib.data.cache;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

import htsjdk.samtools.AlignmentBlock;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;

public class UniqueBaseCallDataCache<T extends AbstractData & hasBaseCallCount> 
extends AbstractUniqueDataCache<T> {
	
	private BaseCallDataCache<T> baseCallDataCache;
	
	public UniqueBaseCallDataCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {
		super(coordinateController);
		
		baseCallDataCache = new BaseCallDataCache<T>(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			baseCallDataCache.incrementBaseCalls(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		baseCallDataCache.incrementBaseCalls(referencePosition, readPosition, length, recordWrapper);
	}
	
	protected void incrementBaseCall(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		if (! getVisited()[readPosition]) {
			baseCallDataCache.incrementBaseCall(windowPosition, readPosition, baseIndex, bq);
			getVisited()[readPosition] = true;
		}
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		baseCallDataCache.addData(data, coordinate);
	}
	
	@Override
	public void clear() {
		baseCallDataCache.clear();
	}

}
