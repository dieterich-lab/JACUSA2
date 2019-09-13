package lib.data.count;

import java.util.EnumMap;
import java.util.Map;

import lib.cli.options.filter.has.BaseSub;
import lib.data.Data;
import lib.data.count.basecall.BaseCallCount;

public class BaseSub2BCC implements Data<BaseSub2BCC> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSub, BaseCallCount> count;
	
	public BaseSub2BCC() {
		count = new EnumMap<>(BaseSub.class);
	}
	
	public BaseSub2BCC(final BaseSub2BCC bsc) {
		this();
		for (final BaseSub baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}

	public BaseSub2BCC newInstance() {
		return new BaseSub2BCC();
	}
	
	@Override
	public BaseSub2BCC copy() {
		return new BaseSub2BCC(this);
	}
	
	@Override
	public void merge(BaseSub2BCC bsc) {
		for (final BaseSub baseSub : bsc.count.keySet()) {
			if (! count.containsKey(baseSub)) {
				count.put(
						baseSub, 
						bsc.get(baseSub).copy() );				
			} else {
				count.get(baseSub).merge(bsc.get(baseSub));
			}
		}
	}
	
	public BaseSub2BCC set(final BaseSub baseSub, final BaseCallCount bcc) {
		if (count.containsKey(baseSub)) {
			throw new IllegalStateException();
		}
		count.put(baseSub, bcc);
		return this;
	}
	
	public BaseCallCount get(final BaseSub baseSub) {
		return count.get(baseSub);
	}
	
}
