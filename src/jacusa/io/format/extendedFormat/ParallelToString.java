package jacusa.io.format.extendedFormat;

import lib.data.ParallelData;

public interface ParallelToString {
    String getId();
    String getDesc();
    String getStringFromParallel(ParallelData parallelData);
}
