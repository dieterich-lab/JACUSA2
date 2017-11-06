package jacusa.pileup.iterator.variant;

import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;

public class AllParallelPileup<T extends AbstractData> implements Variant<T> {

	@Override
	public boolean isValid(ParallelPileupData<T> parallelData) {
		return true;
	}

}
