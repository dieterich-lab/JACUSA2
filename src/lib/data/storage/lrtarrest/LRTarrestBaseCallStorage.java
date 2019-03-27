package lib.data.storage.lrtarrest;

import java.util.HashMap;
import java.util.Map;

import lib.data.DataContainer;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.WindowCoverage;
import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.container.SharedStorage;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

public class LRTarrestBaseCallStorage
extends AbstractStorage 
implements WindowCoverage {

	private final LocationInterpreter li;
	
	private final Fetcher<ArrestPosition2baseCallCount> ap2bccExtractor;
	
	private final ArrestPosition2baseCallCount[] winPos2ap2bcc;
	private final Map<Integer, ArrestPosition2baseCallCount> refPos2ap2bcc;
	
	private final int winSize;
	
	public LRTarrestBaseCallStorage(
			final SharedStorage sharedStorage,
			final LocationInterpreter li, 
			final Fetcher<ArrestPosition2baseCallCount> arrestPos2BaseCallCountExtractor) {
		
		super(sharedStorage);
		this.li 				= li;
		this.ap2bccExtractor 	= arrestPos2BaseCallCountExtractor;
		
		winSize 		= sharedStorage.getCoordinateController().getActiveWindowSize();
		winPos2ap2bcc 	= new ArrestPosition2baseCallCount[winSize];
		refPos2ap2bcc 	= new HashMap<>(Util.noRehashCapacity(200));
	}
	
	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		final int refPos = coordinate.get1Position();
		
		final ArrestPosition2baseCallCount ap2bcc = ap2bccExtractor.fetch(container);
		if (winPos2ap2bcc[winPos] != null) {
			ap2bcc.merge(winPos2ap2bcc[winPos]);
		}
		if (refPos2ap2bcc.containsKey(refPos)) {
			ap2bcc.merge(refPos2ap2bcc.get(refPos));
		}
	}
	
	@Override
	public void increment(Position pos) {
		final Position arrestPos = li.getArrestPosition(
				pos.getRecordExtended(), 
				getCoordinateController().getCoordinateTranslator());

		final ArrestPosition2baseCallCount ap2bcc = get(
				pos.getReferencePosition(), pos.getWindowPosition());
		if (arrestPos == null) {
			ap2bcc.addBaseCall(pos.getReadBaseCall());
		} else {
			ap2bcc.addBaseCall(arrestPos.getReferencePosition(), pos.getReadBaseCall());
		}
	}

	private ArrestPosition2baseCallCount get(final int refPos, final int winPos) {
		ArrestPosition2baseCallCount ap2bcc = null;
		if (winPos >= 0) {
			if (winPos2ap2bcc[winPos] == null) {
				winPos2ap2bcc[winPos] = new ArrestPosition2baseCallCount(refPos);
			}
			ap2bcc = winPos2ap2bcc[winPos];
		} else {
			if (! refPos2ap2bcc.containsKey(refPos)) {
				refPos2ap2bcc.put(refPos, new ArrestPosition2baseCallCount(refPos));
			}
			ap2bcc = refPos2ap2bcc.get(refPos);
		}
		return ap2bcc;
	}
	
	@Override
	public int getCoverage(int winPos) {
		return winPos2ap2bcc[winPos].getTotalBaseCallCount().getCoverage();
	}
	
	@Override
	public void clear() {
		for (final ArrestPosition2baseCallCount ap2bcc : winPos2ap2bcc) {
			if (ap2bcc != null) { 
				ap2bcc.clear();
			}
		}
		if (refPos2ap2bcc.size() > 0) {
			refPos2ap2bcc.clear();
		}
	}
	
}
