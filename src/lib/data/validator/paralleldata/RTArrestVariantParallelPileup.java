package lib.data.validator.paralleldata;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;

public class RTArrestVariantParallelPileup<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasRTcount>
implements ParallelDataValidator<T> {
	
	private VariantSiteValidator<T> variantSite;
	
	public RTArrestVariantParallelPileup() {
		variantSite = new VariantSiteValidator<T>();
	}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		T combinedPooledData = parallelData.getCombinedPooledData();

		return variantSite.isValid(parallelData) || combinedPooledData.getRTarrestCount().getReadArrest() > 0 &&
				combinedPooledData.getRTarrestCount().getReadThrough() > 0;
	}

}