package lib.data.fetcher;

import java.util.Set;

import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.lrtarrest.ArrestPos2BCC;

public class Apply2ReadsArrestPos2BaseCallCountSwitch implements Fetcher<BaseCallCount> {

	private final Set<RT_READS> apply2reads;
	private final Fetcher<ArrestPos2BCC> fetcher;
	
	public Apply2ReadsArrestPos2BaseCallCountSwitch(
			final Set<RT_READS> apply2reads,
			final Fetcher<ArrestPos2BCC> fetcher) {

		this.apply2reads = apply2reads;
		this.fetcher = fetcher;
	}
	
	@Override
	public BaseCallCount fetch(DataContainer container) {
		final ArrestPos2BCC ap2bcc = fetcher.fetch(container);

		if (apply2reads.size() == 2) {
			return container.getBaseCallCount();
		}
		
		final int onePosition = container.getCoordinate().get1Position();
		if (apply2reads.contains(RT_READS.ARREST)) {
			return ap2bcc.getArrestBCC(onePosition);
		} else if (apply2reads.contains(RT_READS.THROUGH)) {
			return ap2bcc.getThroughBCC(onePosition);	
		} else {
			throw new IllegalStateException("apply2reads cannot be empty!");
		}
	}

	public Set<RT_READS> getApply2reads() {
		return apply2reads;
	}
	
}
