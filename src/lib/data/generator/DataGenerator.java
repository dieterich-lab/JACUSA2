package lib.data.generator;

import lib.data.AbstractData;

public interface DataGenerator<T extends AbstractData> {

	public abstract T createData();
	public abstract T[] createReplicateData(final int n);
	public abstract T[][] createContainerData(final int n);

	public abstract T copyData(final T data);
	public abstract T[] copyReplicateData(final T[] replicateData);
	public abstract T[][] copyContainerData(final T[][] containerData);
	
}
