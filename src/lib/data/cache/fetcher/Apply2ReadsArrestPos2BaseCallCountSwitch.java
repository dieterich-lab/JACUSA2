package lib.data.cache.fetcher;

import java.util.Set;

import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.DataTypeContainer;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class Apply2ReadsArrestPos2BaseCallCountSwitch implements Fetcher<BaseCallCount> {

	private final Set<RT_READS> apply2reads;
	private final Fetcher<Position2baseCallCount> fetcher;
	
	public Apply2ReadsArrestPos2BaseCallCountSwitch(
			final Set<RT_READS> apply2reads,
			final Fetcher<Position2baseCallCount> fetcher) {

		this.apply2reads = apply2reads;
		this.fetcher = fetcher;
	}
	
	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final Position2baseCallCount ap2bcc = fetcher.fetch(container);

		if (apply2reads.size() == 2) {
			return container.getBaseCallCount();
		}
		
		final int position = container.getCoordinate().getPosition();
		if (apply2reads.contains(RT_READS.ARREST)) {
			return ap2bcc.getBaseCallCount(position);
		} else if (apply2reads.contains(RT_READS.THROUGH)) {
			return ap2bcc.getBaseCallCountDiff(container.getBaseCallCount());	
		} else {
			throw new IllegalStateException("apply2reads cannot be empty!");
		}
	}

	public Set<RT_READS> getApply2reads() {
		return apply2reads;
	}
	
}
