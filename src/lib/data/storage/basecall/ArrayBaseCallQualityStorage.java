package lib.data.storage.basecall;

import java.util.Arrays;
import java.util.Set;

import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.position.Position;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.data.DataContainer;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.WindowCoverage;
import lib.data.storage.container.SharedStorage;

public class ArrayBaseCallQualityStorage
extends AbstractStorage 
implements WindowCoverage {

	private final Fetcher<PileupCount> pcFetcher;
	private BaseCallQualityCount[] winPos2bcqc;
	
	public ArrayBaseCallQualityStorage(
			final SharedStorage sharedStorage,
			final Fetcher<PileupCount> bcqcFetcher) {
		super(sharedStorage);
		
		this.pcFetcher 	= bcqcFetcher;
		
		final int n  	= getCoordinateController().getActiveWindowSize();
		winPos2bcqc 	= new BaseCallQualityCount[n];
	}
	
	@Override
	public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
		if (winPos2bcqc[winPos] == null) {
			return;
		}

		final Set<Base> alleles 		= winPos2bcqc[winPos].getAlleles();
		final PileupCount pileupCount 	= pcFetcher.fetch(dataContainer);
		pileupCount.getBaseCallQualityCount().add(alleles, winPos2bcqc[winPos]); 
		if (coordinate.getStrand() == STRAND.REVERSE) {
			pileupCount.getBaseCallQualityCount().invert();
		}
	}
	
	@Override
	public void increment(Position pos) {
		final int winPos 	= pos.getWindowPosition();
		final Base base 	= pos.getReadBaseCall();
		final byte baseQual	= pos.getReadBaseCallQuality();
		
		if (winPos2bcqc[winPos] == null) {
			winPos2bcqc[winPos] = BaseCallQualityCount.create();
		}
		final BaseCallQualityCount base2qual2count = winPos2bcqc[winPos];
		base2qual2count.increment(base, baseQual);
	}
	
	@Override
	public void clear() {
		Arrays.fill(winPos2bcqc, null);
	}

	@Override
	public int getCoverage(int winPos) {
		return winPos2bcqc[winPos] == null ? 0 : winPos2bcqc[winPos].getCoverage();
	}
	
}
