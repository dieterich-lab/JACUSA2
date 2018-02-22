package lib.data.generator;

import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public interface DataGenerator<T extends AbstractData> {

	abstract T createData(LIBRARY_TYPE libraryType, final Coordinate coordinate);
	abstract T[] createReplicateData(final int n);
	abstract T[][] createContainerData(final int n);

	abstract T copyData(final T data);
	abstract T[] copyReplicateData(final T[] replicateData);
	abstract T[][] copyContainerData(final T[][] containerData);
	
}
