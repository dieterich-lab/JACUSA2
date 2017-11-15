package lib.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

public class RTArrestVariantParallelPileup<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
implements ParallelDataValidator<T> {
	
	public RTArrestVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		return parallelData.getCombinedPooledData().getReadInfoCount().getArrest() > 0 &&
				parallelData.getCombinedPooledData().getReadInfoCount().getThrough() > 0;
	}

}