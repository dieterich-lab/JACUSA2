package lib.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasReadInfoExtendedCount;
import lib.data.has.hasReferenceBase;

// FIXME make hasReadInfoExtendedCount extend from hasReadInfoCount
public class LinkageRTArrestVariantParallelPileup<T extends AbstractData & hasReferenceBase & hasReadInfoExtendedCount>
implements ParallelDataValidator<T> {
	
	public LinkageRTArrestVariantParallelPileup() {
	}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();
		
		return combinedPooledData.getReadInfoExtendedCount().getArrest() > 0 &&
				combinedPooledData.getReadInfoExtendedCount().getThrough() > 0;
	}

}