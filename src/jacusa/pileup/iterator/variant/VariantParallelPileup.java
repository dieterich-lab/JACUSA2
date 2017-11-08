package jacusa.pileup.iterator.variant;

import lib.data.BaseQualData;
import lib.data.ParallelData;

public class VariantParallelPileup<T extends BaseQualData> 
implements Variant<T> {
	
	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		return parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getAlleles().length > 1;
	}

}
