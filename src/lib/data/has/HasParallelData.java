package lib.data.has;

import lib.data.AbstractData;
import lib.data.ParallelData;

public interface HasParallelData<T extends AbstractData> {

	ParallelData<T> getParellelData();
	
}
