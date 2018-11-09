package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public abstract class AbstractLRTarrestBaseCallAdder
extends AbstractDataContainerAdder 
implements IncrementAdder {

	private final Fetcher<Position2baseCallCount> arrestPos2BaseCallCountExtractor;
	
	private final List<Position2baseCallCount> winPos2arrestPos2bcc;
	private final Map<Integer, Position2baseCallCount> refPos2arrestPos2bc;
	
	private final int N;
	
	public AbstractLRTarrestBaseCallAdder(
			final Fetcher<Position2baseCallCount> arrestPos2BaseCallCountExtractor,
			final SharedCache sharedCache) {
		
		super(sharedCache);
		this.arrestPos2BaseCallCountExtractor = arrestPos2BaseCallCountExtractor;
		
		N = sharedCache.getCoordinateController().getActiveWindowSize();
		winPos2arrestPos2bcc = new ArrayList<>(Collections.nCopies(N, null));
		refPos2arrestPos2bc = new HashMap<>(50);
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int winPos = getCoordinateController().getCoordinateTranslator().convert2windowPosition(coordinate);
		final int refPos = coordinate.getPosition();
		
		final Position2baseCallCount ap2bcc = arrestPos2BaseCallCountExtractor.fetch(container);
		if (winPos2arrestPos2bcc.get(winPos) != null) {
			ap2bcc.merge(winPos2arrestPos2bcc.get(winPos));
		}
		if (refPos2arrestPos2bc.containsKey(refPos)) {
			ap2bcc.merge(refPos2arrestPos2bc.get(refPos));
		}
		
	}
	
	protected void addBaseCall(final int refPos, final int winPos, final int arrestPos, final Base base) {
		if (winPos >= 0) {
			if (winPos2arrestPos2bcc.get(winPos) == null) {
				winPos2arrestPos2bcc.set(winPos, new Position2baseCallCount());
			}
			winPos2arrestPos2bcc.get(winPos).addBaseCall(arrestPos, base);
		} else {
			if (! refPos2arrestPos2bc.containsKey(refPos)) {
				refPos2arrestPos2bc.put(winPos, new Position2baseCallCount());
			}
			refPos2arrestPos2bc.get(refPos).addBaseCall(arrestPos, base);
		}
	}
	
	@Override
	public int getCoverage(int windowPosition) {
		// final int refPos = 
		//		getCoordinateController().getCoordinateTranslator().convert2referencePosition(windowPosition);
		return -1;
	}
	
	@Override
	public void clear() {
		winPos2arrestPos2bcc.clear();
		winPos2arrestPos2bcc.addAll(new ArrayList<>(Collections.nCopies(N, null)));
		refPos2arrestPos2bc.clear();
	}
	
}
