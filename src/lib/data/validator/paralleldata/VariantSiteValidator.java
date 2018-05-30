package lib.data.validator.paralleldata;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;

public class VariantSiteValidator<T extends AbstractData & HasBaseCallCount & HasReferenceBase> 
implements ParallelDataValidator<T> {
	
	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		final T data = parallelData.getCombinedPooledData();
		// more than one non-reference allele
		return data.getBaseCallCount().getAlleles().size() > 1;
	}

}
