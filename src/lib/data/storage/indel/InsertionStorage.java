package lib.data.storage.indel;

import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;

public class InsertionStorage extends AbstractINDELStorage {

	public InsertionStorage(final SharedStorage sharedStorage, final Fetcher<PileupCount> fetcher) {
		super(sharedStorage, fetcher);
	}

	void populate(final PileupCount pileup, final int count) {
		pileup.getINDELCount().addInsertion(count);
	}
	
}
