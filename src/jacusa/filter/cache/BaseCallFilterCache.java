package jacusa.filter.cache;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.BaseCallDataCache;
import lib.data.has.hasBaseCallCount;

public class BaseCallFilterCache<F extends AbstractData & hasBaseCallCount> 
extends AbstractFilterCache<F> {

	private final BaseCallDataCache<F> baseCallcache;

	public BaseCallFilterCache(final char c, 
			final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {

		super(c);
		
		baseCallcache = new BaseCallDataCache<F>(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		baseCallcache.addRecordWrapper(recordWrapper);
	}

	@Override
	public void clear() {
		baseCallcache.clear();
	}

	@Override
	public CoordinateController getCoordinateController() {
		return baseCallcache.getCoordinateController();
	}
	
	@Override
	public void addData(F data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (windowPosition < 0) {
			return;
		}

		for (final int baseIndex : baseCallcache.getBaseCalls()[windowPosition]) {
			data.getBaseCallCount().set(baseIndex, baseCallcache.getBaseCalls()[windowPosition][baseIndex]);
		};
	}
	
}
