package jacusa.pileup.iterator.variant;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasPileupCount;

public class VariantSiteValidator<T extends AbstractData & hasPileupCount> 
implements ParallelDataValidator<T> {
	
	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		T data = parallelData.getCombinedPooledData();
		final int[] allelesIndexs = data.getPileupCount().getAlleles();
		// more than one non-reference allele
		if (allelesIndexs.length > 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		if (data.getReferenceBase() != 'N') {
			byte referfenceBase = data.getReferenceBase();
			final int refBaseIndex = BaseCallConfig.BASES[referfenceBase];

			// there has to be at least one non-reference base call in the data
			return data.getPileupCount().getCoverage() - data.getPileupCount().getBaseCount(refBaseIndex) > 0;
		}

		return false;
	}

}
