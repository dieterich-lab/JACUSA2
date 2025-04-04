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

/**
 * DOCUMENT
 */
public class LRTarrestBaseCallStorage
extends AbstractStorage 
implements WindowCoverage {

	private final LocationInterpreter li;
	
	private final Fetcher<ArrestPos2BCC> ap2bccExtractor;
	
	private final ArrestPos2BCC[] winPos2ap2bcc;
	private final Map<Integer, ArrestPos2BCC> refPos2ap2bcc;
	
	private final int winSize;
	
	public LRTarrestBaseCallStorage(
			final SharedStorage sharedStorage,
			final LocationInterpreter li, 
			final Fetcher<ArrestPos2BCC> arrestPos2BaseCallCountExtractor) {
		
		super(sharedStorage);
		this.li 				= li;
		this.ap2bccExtractor 	= arrestPos2BaseCallCountExtractor;
		
		winSize 		= sharedStorage.getCoordinateController().getActiveWindowSize();
		winPos2ap2bcc 	= new ArrestPos2BCC[winSize];
		refPos2ap2bcc 	= new HashMap<>(Util.noRehashCapacity(200));
	}
	
	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		final int refPos = coordinate.get1Position();
		
		final ArrestPos2BCC ap2bcc = ap2bccExtractor.fetch(container);
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
				pos.getRecord(), 
				getCoordinateController().getCoordinateTranslator());

		final ArrestPos2BCC ap2bcc = get(
				pos.getReferencePosition(), pos.getWindowPosition());
		if (arrestPos == null) {
			ap2bcc.addBaseCall(pos.getReadBaseCall());
		} else {
			ap2bcc.addBaseCall(arrestPos.getReferencePosition(), pos.getReadBaseCall());
		}
	}

	private ArrestPos2BCC get(final int refPos, final int winPos) {
		ArrestPos2BCC ap2bcc = null;
		if (winPos >= 0) {
			if (winPos2ap2bcc[winPos] == null) {
				winPos2ap2bcc[winPos] = new ArrestPos2BCC(refPos);
			}
			ap2bcc = winPos2ap2bcc[winPos];
		} else {
			if (! refPos2ap2bcc.containsKey(refPos)) {
				refPos2ap2bcc.put(refPos, new ArrestPos2BCC(refPos));
			}
			ap2bcc = refPos2ap2bcc.get(refPos);
		}
		return ap2bcc;
	}
	
	@Override
	public int getCoverage(int winPos) {
		return winPos2ap2bcc[winPos].getTotalBCC().getCoverage();
	}
	
	@Override
	public void clear() {
		for (final ArrestPos2BCC ap2bcc : winPos2ap2bcc) {
			if (ap2bcc != null) { 
				ap2bcc.clear();
			}
		}
		if (refPos2ap2bcc.size() > 0) {
			refPos2ap2bcc.clear();
		}
	}
	
}
