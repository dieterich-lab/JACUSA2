package jacusa.pileup.iterator.variant;

import lib.data.AbstractData;
import lib.data.ParallelData;

public class AllParallelPileup<T extends AbstractData> implements Variant<T> {

	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		return true;
	}

}
