package lib.data.adder;

import lib.data.DataTypeContainer;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public interface DataContainerPopulator {

	void populate(DataTypeContainer container, final Coordinate coordinate);
	void clear();
	
	SharedCache getShareCache();
	
	default CoordinateController getCoordinateController() {
		return getShareCache().getCoordinateController();
	}
	
	default ReferenceProvider getReferenceProvider() {
		return getShareCache().getReferenceProvider();
	}
	
}
