package jacusa.pileup.iterator.variant;

import lib.data.PileupReadInfoData;
import lib.data.ParallelData;

public class RTArrestVariantParallelPileup<T extends PileupReadInfoData> 
implements ParallelDataValidator<T> {
	
	public RTArrestVariantParallelPileup() {}

	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		return parallelData.getCombinedPooledData().getReadInfoCount().getArrest() > 0 &&
				parallelData.getCombinedPooledData().getReadInfoCount().getThrough() > 0;
	}

}