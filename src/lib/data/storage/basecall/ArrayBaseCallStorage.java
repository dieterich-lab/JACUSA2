package lib.data.storage.basecall;

import java.util.Arrays;

import htsjdk.samtools.util.SequenceUtil;
import lib.util.Base;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.Storage;

public class ArrayBaseCallStorage
extends AbstractBaseCallCountStorage 
implements Storage {

	private final int[][] baseCalls;
	
	public ArrayBaseCallStorage(
			final SharedStorage sharedStorage,
			final Fetcher<BaseCallCount> bccFetcher) {

		super(sharedStorage, bccFetcher);
		baseCalls = new int[getCoordinateController().getActiveWindowSize()][SequenceUtil.VALID_BASES_UPPER.length];
	}

	@Override
	void increment(int winPos, Base base) {
		baseCalls[winPos][base.getIndex()] += 1;
	}
	
	@Override
	public void clear() {
		for (int[] baseCall : baseCalls) {
			Arrays.fill(baseCall, 0);	
		}
	}

	@Override
	public int getCount(int winPos, Base base) {
		return baseCalls[winPos][base.getIndex()];
	}
	
}
