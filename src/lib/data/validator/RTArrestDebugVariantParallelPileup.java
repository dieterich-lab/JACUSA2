package lib.data.validator;

import lib.data.BaseCallReadInfoData;
import lib.data.ParallelData;

public class RTArrestDebugVariantParallelPileup<T extends BaseCallReadInfoData> 
implements ParallelDataValidator<T> {
	
	public RTArrestDebugVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		int i = 0;
		if (parallelData.getCombinedPooledData().getReadInfoCount().getEnd() > 0) {
			++i;
		}
		if (parallelData.getCombinedPooledData().getReadInfoCount().getInner() > 0) {
			++i;
		}

		return i >= 2;
	}

}