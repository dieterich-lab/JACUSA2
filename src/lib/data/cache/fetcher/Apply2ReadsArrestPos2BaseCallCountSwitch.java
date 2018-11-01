package lib.data.cache.fetcher;

import java.util.Set;

import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.DataTypeContainer;
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class Apply2ReadsArrestPos2BaseCallCountSwitch implements Fetcher<BaseCallCount> {

	private final Set<RT_READS> apply2reads;
	private final Fetcher<ArrestPos2BaseCallCount> fetcher;
	
	public Apply2ReadsArrestPos2BaseCallCountSwitch(
			final Set<RT_READS> apply2reads,
			final Fetcher<ArrestPos2BaseCallCount> fetcher) {

		this.apply2reads = apply2reads;
		this.fetcher = fetcher;
	}
	
	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final ArrestPos2BaseCallCount ap2bcc = fetcher.fetch(container);

		if (apply2reads.size() == 2) {
			return ap2bcc.getTotalBaseCallCount();
		}
		
		final int position = container.getCoordinate().getPosition();
		if (apply2reads.contains(RT_READS.ARREST)) {
			return ap2bcc.getArrestBaseCallCount(position);
		} else if (apply2reads.contains(RT_READS.THROUGH)) {
			return ap2bcc.getThroughBaseCallCount(position);	
		} else {
			throw new IllegalStateException("apply2reads cannot be empty!");
		}
	}

	public Set<RT_READS> getApply2reads() {
		return apply2reads;
	}
	
}
