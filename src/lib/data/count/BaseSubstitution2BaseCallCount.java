package lib.data.count;

import java.util.HashMap;
import java.util.Map;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.Data;
import lib.data.count.basecall.BaseCallCount;

public class BaseSubstitution2BaseCallCount implements Data<BaseSubstitution2BaseCallCount> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSubstitution, BaseCallCount> count;
	
	public BaseSubstitution2BaseCallCount() {
		count = new HashMap<>();
	}
	
	public BaseSubstitution2BaseCallCount(final BaseSubstitution2BaseCallCount bsc) {
		this();
		for (final BaseSubstitution baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}
	
	@Override
	public BaseSubstitution2BaseCallCount copy() {
		return new BaseSubstitution2BaseCallCount(this);
	}
	
	@Override
	public void merge(BaseSubstitution2BaseCallCount bsc) {
		for (final BaseSubstitution baseSubstitution : bsc.count.keySet()) {
			if (! count.containsKey(baseSubstitution)) {
				count.put(
						baseSubstitution, 
						bsc.get(baseSubstitution).copy() );				
			} else {
				count.get(baseSubstitution).merge(bsc.get(baseSubstitution));
			}
		}
	}

	/*
	public BaseSubstitutionCount add(final BaseSubstitution baseSubstitution, final Base base) {
		count.get(baseSubstitution).increment(base);
		return this;
	}
	*/

	public BaseSubstitution2BaseCallCount set(final BaseSubstitution baseSubstitution, final BaseCallCount baseCallCount) {
		if (count.containsKey(baseSubstitution)) {
			throw new IllegalStateException();
		}
		count.put(baseSubstitution, baseCallCount);
		return this;
	}
	
	public BaseCallCount get(final BaseSubstitution baseSubstitution) {
		return count.get(baseSubstitution);
	}
	
}
