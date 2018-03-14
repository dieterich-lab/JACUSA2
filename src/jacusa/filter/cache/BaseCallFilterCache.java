package jacusa.filter.cache;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.BaseCallDataCache;
import lib.data.has.hasBaseCallCount;

/**
 * Implements FilterCache for base calls base on BaseCallDataCache class.
 *
 * @param <T>
 */
public class BaseCallFilterCache<T extends AbstractData & hasBaseCallCount> 
extends AbstractFilterCache<T> {

	private final BaseCallDataCache<T> baseCallcache;

	public BaseCallFilterCache(final char c, 
			final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {

		super(c);
		baseCallcache = new BaseCallDataCache<T>(maxDepth, minBASQ, 
				baseCallConfig, coordinateController);
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
	public void addData(T data, Coordinate coordinate) {
		baseCallcache.addData(data, coordinate);

		/* TODO test if the upper replacement works
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (windowPosition < 0) {
			return;
		}
		
		for (final int baseIndex : baseCallcache.getBaseCalls()[windowPosition]) {
			data.getBaseCallCount().set(baseIndex, baseCallcache.getBaseCalls()[windowPosition][baseIndex]);
		}
		*/
	}
	
}
