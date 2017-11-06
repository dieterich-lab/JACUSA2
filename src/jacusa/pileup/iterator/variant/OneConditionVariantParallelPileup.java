package jacusa.pileup.iterator.variant;

import jacusa.data.BaseCallConfig;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;

public class OneConditionVariantParallelPileup<T extends BaseQualData> 
implements Variant<T> {
	
	@Override
	public boolean isValid(ParallelPileupData<T> parallelData) {
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
