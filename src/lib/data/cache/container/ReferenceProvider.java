package lib.data.cache.container;

import lib.util.coordinate.Coordinate;

public interface ReferenceProvider {

	byte getReference(Coordinate coordinate);
	byte getReference(int windowPosition);

	void update();
	
}
