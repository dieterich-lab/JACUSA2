package lib.data.cache.extractor;

import lib.data.AbstractData;
import lib.data.cache.container.ReferenceProvider;
import lib.util.coordinate.Coordinate;

public interface ReferenceSetter<T extends AbstractData> {

	void setReference(Coordinate coordinate, T data, ReferenceProvider referenceProvider);
	
}
