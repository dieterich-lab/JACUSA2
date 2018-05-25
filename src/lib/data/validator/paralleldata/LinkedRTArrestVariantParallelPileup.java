package lib.data.validator.paralleldata;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;

public class LinkedRTArrestVariantParallelPileup<T extends AbstractData & HasReferenceBase & HasLRTarrestCount>
implements ParallelDataValidator<T> {

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();
		
		return combinedPooledData.getLRTarrestCount().getRTarrestCount().getReadArrest() > 0 &&
				combinedPooledData.getLRTarrestCount().getRTarrestCount().getReadThrough() > 0;
	}

}