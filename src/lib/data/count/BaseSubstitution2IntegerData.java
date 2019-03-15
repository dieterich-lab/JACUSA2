package lib.data.count;

import java.util.HashMap;
import java.util.Map;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.Data;
import lib.data.IntegerData;

public class BaseSubstitution2IntegerData implements Data<BaseSubstitution2IntegerData> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSubstitution, IntegerData> count;
	
	public BaseSubstitution2IntegerData() {
		count = new HashMap<>();
	}
	
	public BaseSubstitution2IntegerData(final BaseSubstitution2IntegerData bsc) {
		this();
		for (final BaseSubstitution baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}
	
	@Override
	public BaseSubstitution2IntegerData copy() {
		return new BaseSubstitution2IntegerData(this);
	}
	
	@Override
	public void merge(BaseSubstitution2IntegerData bsc) {
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

	public BaseSubstitution2IntegerData set(final BaseSubstitution baseSubstitution, final IntegerData integerData) {
		if (count.containsKey(baseSubstitution)) {
			throw new IllegalStateException();
		}
		count.put(baseSubstitution, integerData);
		return this;
	}
	
	public IntegerData get(final BaseSubstitution baseSubstitution) {
		return count.get(baseSubstitution);
	}
	
}
