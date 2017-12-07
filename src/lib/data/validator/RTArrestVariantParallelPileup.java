package lib.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasReferenceBase;

public class RTArrestVariantParallelPileup<T extends AbstractData & hasBaseCallCount & hasReferenceBase & hasReadInfoCount>
implements ParallelDataValidator<T> {
	
	private VariantSiteValidator<T> variantSite;
	
	public RTArrestVariantParallelPileup() {
		variantSite = new VariantSiteValidator<T>();
	}
	

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();
		
		return variantSite.isValid(parallelData) || combinedPooledData.getReadInfoCount().getArrest() > 0 &&
				combinedPooledData.getReadInfoCount().getThrough() > 0;
	}

}