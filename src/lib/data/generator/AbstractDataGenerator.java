package lib.data.generator;

import lib.data.AbstractData;

public abstract class AbstractDataGenerator<T extends AbstractData> 
implements DataGenerator<T> {

	@Override
	public final T[] copyReplicateData(final T[] dataContainer) {
		T[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = copyData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public final T[][] copyContainerData(final T[][] dataContainer) {
		T[][] ret = createContainerData(dataContainer.length);
		
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = createReplicateData(dataContainer[i].length);
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = copyData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}

}
