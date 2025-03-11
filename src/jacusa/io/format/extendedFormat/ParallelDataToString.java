package jacusa.io.format.extendedFormat;

import lib.data.ParallelData;

public interface ParallelDataToString {
    String getId();
    String getDesc();
    String getStringFromParallel(ParallelData parallelData);
}
