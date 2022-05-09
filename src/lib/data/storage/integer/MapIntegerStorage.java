package lib.data.storage.integer;

import java.util.HashMap;
import java.util.Map;

import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.storage.container.SharedStorage;
import lib.util.position.Position;

public class MapIntegerStorage extends AbstractIntegerStorage {

	final Map<Integer, Integer> win2count;

	public MapIntegerStorage(final SharedStorage sharedStorage, final DataType<IntegerData> dataType) {
		super(sharedStorage, dataType);
		win2count = new HashMap<>();
	}
		
	@Override
	public void increment(Position position) {
		final int winPos = position.getWindowPosition();
		final int count = getCount(winPos) + 1;
		win2count.put(winPos, count);
	}

	@Override
	public void clear() {
		win2count.clear();
	}

	@Override
	public int getCount(int winPos) {
		return win2count.containsKey(winPos) ? win2count.get(winPos) : 0; 
	}

}
