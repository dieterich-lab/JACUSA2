package lib.data.cache;

import lib.util.coordinate.CoordinateController;

import htsjdk.samtools.AlignmentBlock;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;

public class UniqueBaseCallDataCache<T extends AbstractData & hasBaseCallCount> 
extends BaseCallDataCache<T> {
	
	private boolean[] visited;
	
	public UniqueBaseCallDataCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {
		super(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			incrementBaseCalls(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		incrementBaseCalls(referencePosition, readPosition, length, recordWrapper);
	}
	
	protected void incrementBaseCall(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		if (! visited[readPosition]) {
			super.incrementBaseCall(windowPosition, readPosition, baseIndex, bq);
			visited[readPosition] = true;
		}
	}

	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}
	
}
