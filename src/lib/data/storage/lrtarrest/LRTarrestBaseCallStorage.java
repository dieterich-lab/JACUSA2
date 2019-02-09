package lib.data.storage.lrtarrest;

import java.util.HashMap;
import java.util.Map;

import lib.data.DataContainer;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.AbstractStorage;
import lib.data.stroage.WindowCoverage;
import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

public class LRTarrestBaseCallStorage
extends AbstractStorage 
implements WindowCoverage {

	private final ArrestPositionCalculator apc;
	
	private final Fetcher<ArrestPosition2baseCallCount> ap2bccExtractor;
	
	private final ArrestPosition2baseCallCount[] winPos2ap2bcc;
	private final Map<Integer, ArrestPosition2baseCallCount> refPos2ap2bcc;
	
	private final int winSize;
	
	public LRTarrestBaseCallStorage(
			final SharedStorage sharedStorage,
			final ArrestPositionCalculator arrestPositionCalculator, 
			final Fetcher<ArrestPosition2baseCallCount> arrestPos2BaseCallCountExtractor) {
		
		super(sharedStorage);
		this.apc 				= arrestPositionCalculator;
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
		final int arrestPos = apc.get(pos.getRecord());
		addBaseCall(
				pos.getReferencePosition(), 
				pos.getWindowPosition(), 
				arrestPos, 
				pos.getReadBaseCall());
	}
	
	protected void addBaseCall(
			final int refPos, final int winPos, final int arrestPosition, 
			final Base base) {

		if (winPos >= 0) {
			if (winPos2ap2bcc[winPos] == null) {
				winPos2ap2bcc[winPos] = new ArrestPosition2baseCallCount();
			}
			winPos2ap2bcc[winPos].addBaseCall(arrestPosition, base);
		} else {
			if (! refPos2ap2bcc.containsKey(refPos)) {
				refPos2ap2bcc.put(refPos, new ArrestPosition2baseCallCount());
			}
			refPos2ap2bcc.get(refPos).addBaseCall(arrestPosition, base);
		}
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
