package lib.data.count;

import java.util.EnumMap;
import java.util.Map;

import lib.cli.options.filter.has.BaseSub;
import lib.data.Data;
import lib.data.IntegerData;

public class BaseSub2IntData implements Data<BaseSub2IntData> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSub, IntegerData> count;
	
	public BaseSub2IntData() {
		count = new EnumMap<>(BaseSub.class);
	}
	
	public BaseSub2IntData(final BaseSub2IntData bsc) {
		this();
		for (final BaseSub baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}
	
	@Override
	public BaseSub2IntData copy() {
		return new BaseSub2IntData(this);
	}
	
	@Override
	public void merge(BaseSub2IntData bsc) {
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

	public BaseSub2IntData set(final BaseSub baseSubstitution, final IntegerData integerData) {
		if (count.containsKey(baseSubstitution)) {
			throw new IllegalStateException();
		}
		count.put(baseSubstitution, integerData);
		return this;
	}
	
	public IntegerData get(final BaseSub baseSubstitution) {
		return count.get(baseSubstitution);
	}
	
}
