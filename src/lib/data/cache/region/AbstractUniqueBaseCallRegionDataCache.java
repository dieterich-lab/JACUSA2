package lib.data.cache.region;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractUniqueBaseCallRegionDataCache<T extends AbstractData> 
extends AbstractBaseCallRegionDataCache<T> implements UniqueRegionDataCache<T> {

	private boolean[] visited;

	public AbstractUniqueBaseCallRegionDataCache(final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}
	
	@Override
	public void incrementBaseCall(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		if (! visited[readPosition]) {
			super.incrementBaseCall(windowPosition, readPosition, baseIndex, bq);
			visited[readPosition] = true;
		}
	}

	@Override
	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}

}
