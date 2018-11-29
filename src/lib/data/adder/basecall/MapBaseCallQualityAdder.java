package lib.data.adder.basecall;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.SAMRecord;
import jacusa.JACUSA;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;

public class MapBaseCallQualityAdder
extends AbstractDataContainerPopulator 
implements IncrementAdder {

	private final Fetcher<PileupCount> pcFetcher;
	private Map<Integer, BaseCallQualityCount> baseCallQualities;
	
	public MapBaseCallQualityAdder(
			final SharedCache sharedCache,
			final Fetcher<PileupCount> bcqcFetcher) {
		super(sharedCache);
		
		this.pcFetcher = bcqcFetcher;
		
		final int n  = getCoordinateController().getActiveWindowSize();
		baseCallQualities = new HashMap<Integer, BaseCallQualityCount>(n / 2);
	}

	@Override
	public int getCoverage(int windowPosition) {
		if (! baseCallQualities.containsKey(windowPosition)) {
			return 0;
		}
		return baseCallQualities.get(windowPosition).getCoverage();
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().getCoordinateTranslator().convert2windowPosition(coordinate);
		if (! baseCallQualities.containsKey(windowPosition)) {
			return;
		}

		final Set<Base> alleles = baseCallQualities.get(windowPosition).getAlleles();
		final PileupCount pileupCount = pcFetcher.fetch(container);
		pileupCount.getBaseCallQualityCount().add(alleles, baseCallQualities.get(windowPosition)); 
		if (coordinate.getStrand() == STRAND.REVERSE) {
			pileupCount.getBaseCallQualityCount().invert();
		}
	}
	
	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition,
			final Base base, final byte baseQual,
			final SAMRecord record) {

		if (! baseCallQualities.containsKey(windowPosition)) {
			baseCallQualities.put(windowPosition, JACUSA.bcqcFactory.create());
		}
		final BaseCallQualityCount base2qual2count = baseCallQualities.get(windowPosition);
		base2qual2count.increment(base, baseQual);
	}
	
	@Override
	public void clear() {
		baseCallQualities.clear();
	}

}
