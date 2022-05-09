package lib.data.storage.basecall;

import java.util.HashMap;
import java.util.Map;

import lib.util.Base;
import lib.util.Util;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.Storage;
import lib.data.storage.container.SharedStorage;

public class MapBaseCallStorage
extends AbstractBaseCallCountStorage
implements Storage {

	private final Map<Integer, BaseCallCount> winPos2bcc;

	public MapBaseCallStorage(final SharedStorage sharedStorage,final DataType<BaseCallCount> dataType) {
		super(sharedStorage, dataType);

		final int n = Util.noRehashCapacity(getCoordinateController().getActiveWindowSize() / 2);
		winPos2bcc 	= new HashMap<>(n);
	}

	@Override
	void increment(int winPos, Base base) {
		if (! winPos2bcc.containsKey(winPos)) {
			winPos2bcc.put(winPos, BaseCallCount.create());
		}
		winPos2bcc.get(winPos).increment(base);
	}

	@Override
	public int getCount(int winPos, Base base) {
		return ! winPos2bcc.containsKey(winPos) ? 0 : winPos2bcc.get(winPos).getBaseCall(base);
	}
	
	@Override
	protected void clearSpecific() {
		winPos2bcc.clear();
	}
	
}
