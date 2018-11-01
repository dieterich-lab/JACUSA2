package lib.stat.dirmult;

import lib.data.ParallelData;

public interface DirMultSampleProvider {

	DirMultSample[] convert(ParallelData parallelData);

}
