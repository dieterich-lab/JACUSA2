package lib.data.cache;

import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractDataCache<T extends AbstractData>
implements DataCache<T> {
	
	private final CoordinateController coordinateController;
	
	public AbstractDataCache(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
	}
	
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
}
