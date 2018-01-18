package lib.data.validator;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class VariantSiteValidator<T extends AbstractData & hasBaseCallCount & hasReferenceBase> 
implements ParallelDataValidator<T> {
	
	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		T data = parallelData.getCombinedPooledData();
		final int[] allelesIndexs = data.getBaseCallCount().getAlleles();
		// more than one non-reference allele
		if (allelesIndexs.length > 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		byte referenceBase = data.getReferenceBase();
		if (referenceBase != 'N') {
			
			final int refBaseIndex = BaseCallConfig.getInstance().getBaseIndex(referenceBase);

			// there has to be at least one non-reference base call in the data
			return data.getBaseCallCount().getCoverage() - data.getBaseCallCount().getBaseCallCount(refBaseIndex) > 0;
		}

		return false;
	}

}
