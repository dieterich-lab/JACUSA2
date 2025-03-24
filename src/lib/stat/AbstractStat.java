package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;
import lib.util.ExtendedInfo;

public abstract class AbstractStat {

	// can be null
	public abstract Result process(ParallelData parallelData, ExtendedInfo info);
	
}
