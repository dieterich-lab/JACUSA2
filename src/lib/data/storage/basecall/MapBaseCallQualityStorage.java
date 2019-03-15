package lib.data.storage.basecall;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.position.Position;
import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.data.DataContainer;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.WindowCoverage;
import lib.data.storage.container.SharedStorage;

public class MapBaseCallQualityStorage
extends AbstractStorage 
implements WindowCoverage {

	private final Fetcher<PileupCount> pcFetcher;
	private Map<Integer, BaseCallQualityCount> winPos2bcqc;
	
	public MapBaseCallQualityStorage(
			final SharedStorage sharedStorage,
			final Fetcher<PileupCount> pileupFetcher) {
		super(sharedStorage);
		
		this.pcFetcher = pileupFetcher;
		
		final int n  = Util.noRehashCapacity(getCoordinateController().getActiveWindowSize() / 2);
		winPos2bcqc = new HashMap<Integer, BaseCallQualityCount>(n);
	}
	
	@Override
	public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
		if (! winPos2bcqc.containsKey(winPos)) {
			return;
		}

		final Set<Base> alleles = winPos2bcqc.get(winPos).getAlleles();
		final PileupCount pileupCount = pcFetcher.fetch(dataContainer);
		pileupCount.getBaseCallQualityCount().add(alleles, winPos2bcqc.get(winPos)); 
		if (coordinate.getStrand() == STRAND.REVERSE) {
			pileupCount.getBaseCallQualityCount().invert();
		}
	}
	
	@Override
	public void increment(Position pos) {
		final int winPos 	= pos.getWindowPosition();
		final Base base 	= pos.getReadBaseCall();
		final byte baseQual	= pos.getReadBaseCallQuality();
		
		if (! winPos2bcqc.containsKey(winPos)) {
			winPos2bcqc.put(winPos, BaseCallQualityCount.create());
		}
		final BaseCallQualityCount base2qual2count = winPos2bcqc.get(winPos);
		base2qual2count.increment(base, baseQual);
	}
	
	@Override
	public void clear() {
		winPos2bcqc.clear();
	}

	@Override
	public int getCoverage(int winPos) {
		if (! winPos2bcqc.containsKey(winPos)) {
			return 0;
		}
		return winPos2bcqc.get(winPos).getCoverage();
	}
	
}
