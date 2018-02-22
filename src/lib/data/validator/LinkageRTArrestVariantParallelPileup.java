package lib.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasLinkedReadArrestCount;
import lib.data.has.hasReferenceBase;

// FIXME make hasReadInfoExtendedCount extend from hasReadInfoCount
public class LinkageRTArrestVariantParallelPileup<T extends AbstractData & hasReferenceBase & hasLinkedReadArrestCount>
implements ParallelDataValidator<T> {
	
	public LinkageRTArrestVariantParallelPileup() {
	}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();
		
		return combinedPooledData.getLinkedReadArrestCount().getReadArrestCount().getReadArrest() > 0 &&
				combinedPooledData.getLinkedReadArrestCount().getReadArrestCount().getReadThrough() > 0;
	}

}