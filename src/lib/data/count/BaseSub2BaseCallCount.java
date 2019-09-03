package lib.data.count;

import java.util.EnumMap;
import java.util.Map;

import lib.cli.options.filter.has.BaseSub;
import lib.data.Data;
import lib.data.count.basecall.BaseCallCount;

public class BaseSub2BaseCallCount implements Data<BaseSub2BaseCallCount> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSub, BaseCallCount> count;
	
	public BaseSub2BaseCallCount() {
		count = new EnumMap<>(BaseSub.class);
	}
	
	public BaseSub2BaseCallCount(final BaseSub2BaseCallCount bsc) {
		this();
		for (final BaseSub baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}

	public BaseSub2BaseCallCount newInstance() {
		return new BaseSub2BaseCallCount();
	}
	
	@Override
	public BaseSub2BaseCallCount copy() {
		return new BaseSub2BaseCallCount(this);
	}
	
	@Override
	public void merge(BaseSub2BaseCallCount bsc) {
		for (final BaseSub baseSubstitution : bsc.count.keySet()) {
			if (! count.containsKey(baseSubstitution)) {
				count.put(
						baseSubstitution, 
						bsc.get(baseSubstitution).copy() );				
			} else {
				count.get(baseSubstitution).merge(bsc.get(baseSubstitution));
			}
		}
	}
	
	public BaseSub2BaseCallCount set(final BaseSub baseSubstitution, final BaseCallCount baseCallCount) {
		if (count.containsKey(baseSubstitution)) {
			throw new IllegalStateException();
		}
		count.put(baseSubstitution, baseCallCount);
		return this;
	}
	
	public BaseCallCount get(final BaseSub baseSubstitution) {
		return count.get(baseSubstitution);
	}
	
}
