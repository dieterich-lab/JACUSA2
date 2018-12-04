package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htsjdk.samtools.SAMRecord;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public class LRTarrestBaseCallAdder
extends AbstractDataContainerPopulator 
implements IncrementAdder {

	private final ArrestPositionCalculator apc;
	
	private final Fetcher<ArrestPosition2baseCallCount> ap2bccExtractor;
	
	private final List<ArrestPosition2baseCallCount> winPos2ap2bcc;
	private final Map<Integer, ArrestPosition2baseCallCount> refPos2ap2bcc;
	
	private final int winSize;
	
	public LRTarrestBaseCallAdder(
			final SharedCache sharedCache,
			final ArrestPositionCalculator arrestPositionCalculator, 
			final Fetcher<ArrestPosition2baseCallCount> arrestPos2BaseCallCountExtractor) {
		
		super(sharedCache);
		this.apc = arrestPositionCalculator;
		this.ap2bccExtractor = arrestPos2BaseCallCountExtractor;
		
		winSize = sharedCache.getCoordinateController().getActiveWindowSize();
		winPos2ap2bcc = new ArrayList<>(Collections.nCopies(winSize, null));
		refPos2ap2bcc = new HashMap<>(50);
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int winPos = getCoordinateController().getCoordinateTranslator().convert2windowPosition(coordinate);
		final int refPos = coordinate.getPosition();
		
		final ArrestPosition2baseCallCount ap2bcc = ap2bccExtractor.fetch(container);
		if (winPos2ap2bcc.get(winPos) != null) {
			ap2bcc.merge(winPos2ap2bcc.get(winPos));
		}
		if (refPos2ap2bcc.containsKey(refPos)) {
			ap2bcc.merge(refPos2ap2bcc.get(refPos));
		}
	}
	
	@Override
	public void increment(
			int referencePosition, int windowPosition, int readPosition, 
			Base base, byte baseQuality,
			SAMRecord record) {
		
		final int arrestPos = apc.get(record);
		addBaseCall(referencePosition, windowPosition, arrestPos, base);
	}
	
	protected void addBaseCall(
			final int referencePosition, final int windowPosition, final int arrestPosition, 
			final Base base) {

		if (windowPosition >= 0) {
			if (winPos2ap2bcc.get(windowPosition) == null) {
				winPos2ap2bcc.set(windowPosition, new ArrestPosition2baseCallCount());
			}
			winPos2ap2bcc.get(windowPosition).addBaseCall(arrestPosition, base);
		} else {
			if (! refPos2ap2bcc.containsKey(referencePosition)) {
				refPos2ap2bcc.put(referencePosition, new ArrestPosition2baseCallCount());
			}
			refPos2ap2bcc.get(referencePosition).addBaseCall(arrestPosition, base);
		}
	}
	
	@Override
	public int getCoverage(int windowPosition) {
		return winPos2ap2bcc.get(windowPosition).getTotalBaseCallCount().getCoverage();
	}
	
	@Override
	public void clear() {
		for (final ArrestPosition2baseCallCount ap2bcc : winPos2ap2bcc) {
			if (ap2bcc != null) { 
				ap2bcc.clear();
			}
		}
		refPos2ap2bcc.clear();
	}
	
}
