package lib.data.generator;

import lib.data.AbstractData;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public interface DataGenerator<T extends AbstractData> {

	T createData(LIBRARY_TYPE libraryType, final Coordinate coordinate);
	T[] createReplicateData(final int n);
	T[][] createContainerData(final int n);

	T copyData(final T data);
	T[] copyReplicateData(final T[] replicateData);
	T[][] copyContainerData(final T[][] containerData);
	
	void merge(T dest, T src);
}
