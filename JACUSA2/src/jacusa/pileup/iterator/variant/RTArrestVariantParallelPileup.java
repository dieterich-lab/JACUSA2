package jacusa.pileup.iterator.variant;

import jacusa.data.BaseQualReadInfoData;
import jacusa.data.ParallelPileupData;

public class RTArrestVariantParallelPileup<T extends BaseQualReadInfoData> 
implements Variant<T> {
	
	public RTArrestVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelPileupData<T> parallelData) {
		return parallelData.getCombinedPooledData().getReadInfoCount().getArrest() > 0 &&
				parallelData.getCombinedPooledData().getReadInfoCount().getThrough() > 0;
	}

}