package jacusa.io.format.extensions;

import lib.data.ParallelData;

public interface ParallelDataToString {
	
    String getID();
    String getDesc();
    String toString(ParallelData parallelData);
}
