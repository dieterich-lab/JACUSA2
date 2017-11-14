package lib.data.generator;

import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.Coordinate;

public interface DataGenerator<T extends AbstractData> {

	public abstract T createData(LIBRARY_TYPE libraryType, final Coordinate coordinate);
	public abstract T[] createReplicateData(final int n);
	public abstract T[][] createContainerData(final int n);

	public abstract T copyData(final T data);
	public abstract T[] copyReplicateData(final T[] replicateData);
	public abstract T[][] copyContainerData(final T[][] containerData);
	
}
