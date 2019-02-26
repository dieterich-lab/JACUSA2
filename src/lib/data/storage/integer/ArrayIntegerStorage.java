package lib.data.storage.integer;

import java.util.Arrays;

import lib.data.IntegerData;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.util.position.Position;

public class ArrayIntegerStorage extends AbstractIntegerStorage {

	final int[] win2count;

	public ArrayIntegerStorage(final SharedStorage sharedStorage, final Fetcher<IntegerData> fetcher) {
		super(sharedStorage, fetcher);
		win2count = new int [sharedStorage.getCoordinateController().getActiveWindowSize()];
	}
	
	@Override
	public void increment(Position position) {
		final int winPos = position.getWindowPosition();
		final int count = getCount(winPos) + 1;
		win2count[winPos] = count;
	}

	@Override
	public void clear() {
		Arrays.fill(win2count, 0);
	}

	@Override
	public int getCount(int winPos) {
		return win2count[winPos]; 
	}

}
