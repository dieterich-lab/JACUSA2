package lib.data.validator;

import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;

public class VariantSiteValidator<T extends AbstractData & HasBaseCallCount & HasReferenceBase> 
implements ParallelDataValidator<T> {
	
	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		final T data = parallelData.getCombinedPooledData();
		final Set<Integer> alleles = data.getBaseCallCount().getAlleles();
		// more than one non-reference allele
		if (alleles.size()> 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		byte referenceBase = data.getReferenceBase();
		if (referenceBase != 0 && referenceBase != 'N') {
			
			final int refBaseIndex = BaseCallConfig.getInstance().getBaseIndex(referenceBase);

			// there has to be at least one non-reference base call in the data
			return data.getBaseCallCount().getCoverage() - data.getBaseCallCount().getBaseCall(refBaseIndex) > 0;
		}

		return false;
	}

}
