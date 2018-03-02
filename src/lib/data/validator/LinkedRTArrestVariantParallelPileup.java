package lib.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;

public class LinkedRTArrestVariantParallelPileup<T extends AbstractData & hasReferenceBase & hasLRTarrestCount>
implements ParallelDataValidator<T> {

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();
		
		return combinedPooledData.getLRTarrestCount().getRTarrestCount().getReadArrest() > 0 &&
				combinedPooledData.getLRTarrestCount().getRTarrestCount().getReadThrough() > 0;
	}

}