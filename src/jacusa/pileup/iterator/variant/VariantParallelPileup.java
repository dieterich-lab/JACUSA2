package jacusa.pileup.iterator.variant;

import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;

public class VariantParallelPileup<T extends BaseQualData> 
implements Variant<T> {
	
	@Override
	public boolean isValid(ParallelPileupData<T> parallelData) {
		return parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getAlleles().length > 1;
	}

}
