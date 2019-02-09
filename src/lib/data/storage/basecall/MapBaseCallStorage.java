package lib.data.storage.basecall;

import java.util.HashMap;
import java.util.Map;

import jacusa.JACUSA;
import lib.util.Base;
import lib.util.position.Position;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.Storage;

public class MapBaseCallStorage
extends AbstractBaseCallCountStorage
implements Storage {

	private final Map<Integer, BaseCallCount> winPos2bcc;

	public MapBaseCallStorage(
			final SharedStorage sharedStorage,final Fetcher<BaseCallCount> bccFetcher) {

		super(sharedStorage, bccFetcher);
		final int n = getCoordinateController().getActiveWindowSize();
		winPos2bcc 	= new HashMap<Integer, BaseCallCount>(n);
	}
	
	@Override
	public void increment(Position pos) {
		final int winPos 	= pos.getWindowPosition();
		final Base base 	= pos.getReadBaseCall();
		
		if (! winPos2bcc.containsKey(winPos)) {
			winPos2bcc.put(winPos, JACUSA.BCC_FACTORY.create());
		}
		winPos2bcc.get(winPos).increment(base);
	}

	@Override
	public int getCount(int winPos, Base base) {
		return ! winPos2bcc.containsKey(winPos) ? 0 : winPos2bcc.get(winPos).getBaseCall(base);
	}
	
	@Override
	public void clear() {
		winPos2bcc.clear();
	}
	
}