package lib.data.validator;

import lib.data.RTarrestData;
import lib.data.ParallelData;

public class RTArrestDebugVariantParallelPileup<T extends RTarrestData> 
implements ParallelDataValidator<T> {
	
	public RTArrestDebugVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		int i = 0;
		if (parallelData.getCombinedPooledData().getRTarrestCount().getReadEnd() > 0) {
			++i;
		}
		if (parallelData.getCombinedPooledData().getRTarrestCount().getReadInternal() > 0) {
			++i;
		}

		return i >= 2;
	}

}