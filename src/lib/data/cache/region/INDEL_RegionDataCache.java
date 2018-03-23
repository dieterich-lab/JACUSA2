package lib.data.cache.region;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.has.filter.hasINDEL_FilterData;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public class INDEL_RegionDataCache<T extends AbstractData & hasINDEL_FilterData> 
extends AbstractUniqueBaseCallRegionDataCache<T> {

	public INDEL_RegionDataCache(int maxDepth, byte minBASQ,
			BaseCallConfig baseCallConfig,
			CoordinateController coordinateController) {
		super(maxDepth, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		final BaseCallCount baseCallCount = new BaseCallCount();
		data.setINDEL_DistanceFilterData(baseCallCount);
		add(windowPosition, coordinate.getStrand(), baseCallCount);
	}	

}
