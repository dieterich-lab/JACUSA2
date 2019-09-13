package lib.data.storage;

import lib.data.DataContainer;
import lib.data.storage.container.ReferenceProvider;
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.position.Position;

/**
 * TODO
 */
public interface Storage {
	
	void increment(Position position);
	void clear();
	
	void populate(DataContainer dataContainer, int winPos, Coordinate coordinate);
	
	SharedStorage getSharedStorage();
	
	// convenience - short cut
	default CoordinateController getCoordinateController() {
		return getSharedStorage().getCoordinateController();
	}
	
	// convenience - short cut
	default ReferenceProvider getReferenceProvider() {
		return getSharedStorage().getReferenceProvider();
	}
	
}