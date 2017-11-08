package jacusa.pileup.iterator.variant;

import lib.data.BaseCallConfig;
import lib.data.BaseQualData;
import lib.data.ParallelData;

public class OneConditionVariantParallelPileup<T extends BaseQualData> 
implements Variant<T> {
	
	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		T data = parallelData.getCombinedPooledData();
		int[] allelesIndexs = data.getBaseQualCount().getAlleles();
		// more than one non-reference allele
		if (allelesIndexs.length > 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		int refBaseIndex = -1;
		if (data.getReferenceBase() != 'N') {
			char refBase = data.getReferenceBase();
			refBaseIndex = BaseCallConfig.BASES[(byte)refBase];

			// there has to be at least one non-reference base call in the data
			return data.getBaseQualCount().getCoverage() - data.getBaseQualCount().getBaseCount(refBaseIndex) > 0;
		}

		return false;
	}

}
