package lib.data.cache.lrtarrest;

import htsjdk.samtools.SAMRecord;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.util.Base;

public class EndLRTarrestBaseCallAdder
extends AbstractLRTarrestBaseCallAdder {

	public EndLRTarrestBaseCallAdder(
			final Fetcher<Position2baseCallCount> arrestPos2BaseCallCountFetcher,
			final SharedCache sharedCache) {
		
		super(arrestPos2BaseCallCountFetcher, sharedCache);
	}

	@Override
	public void increment(
			final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		final int arrestPos = record.getAlignmentEnd();
		addBaseCall(referencePosition, windowPosition, arrestPos, base);
	}
	
	
}
