package jacusa.pileup.iterator.variant;

import lib.data.BaseQualReadInfoData;
import lib.data.ParallelData;

public class RTArrestVariantParallelPileup<T extends BaseQualReadInfoData> 
implements Variant<T> {
	
	public RTArrestVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		return parallelData.getCombinedPooledData().getReadInfoCount().getArrest() > 0 &&
				parallelData.getCombinedPooledData().getReadInfoCount().getThrough() > 0;
	}

}