package lib.data.storage.indel;

import java.util.Arrays;

import lib.data.DataContainer;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

abstract class AbstractINDELStorage extends AbstractStorage {

	private final Fetcher<PileupCount> fetcher;
	private int[] winPos2count;
	
	AbstractINDELStorage(final SharedStorage sharedStorage, final Fetcher<PileupCount> fetcher) {
		super(sharedStorage);
		this.fetcher = fetcher;
		
		final int n  	= getCoordinateController().getActiveWindowSize();
		winPos2count 	= new int[n];
	}

	@Override
	public void increment(Position position) {
		winPos2count[position.getWindowPosition()]++;
	}
	
	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		if (fetcher == null) {
			return;
		}

		final int count = winPos2count[winPos];
		final PileupCount pileup = fetcher.fetch(container);
		
		populate(pileup, count);
	}

	abstract void populate(final PileupCount pileup, final int count);
	
	@Override
	public int hashCode() {
		return getCoordinateController().getActive().hashCode();
	}

	@Override
	public void clear() {
		Arrays.fill(winPos2count, 0);
	}
	
}
