package lib.data.has;

import lib.data.AbstractData;
import lib.data.ParallelData;

public interface hasParallelData<T extends AbstractData> {

	ParallelData<T> getParellelData();
	
}
