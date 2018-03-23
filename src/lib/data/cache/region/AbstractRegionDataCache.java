package lib.data.cache.region;

import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractRegionDataCache<T extends AbstractData>
implements RegionDataCache<T> {
	
	private final CoordinateController coordinateController;
	
	public AbstractRegionDataCache(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
	}
	
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
}
