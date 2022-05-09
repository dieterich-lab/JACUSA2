package lib.data.count;

import lib.cli.options.filter.has.BaseSub;
import lib.data.count.basecall.BaseCallCount;

public class BaseSub2BaseCallCount extends AbstractMappedData<BaseSub2BaseCallCount, BaseSub, BaseCallCount> {

	private static final long serialVersionUID = 1L;

	public BaseSub2BaseCallCount() {
		super(BaseSub.class, BaseCallCount.class);
	}

	public BaseSub2BaseCallCount(BaseSub2BaseCallCount o) {
		super(o);
	}

	@Override
	public BaseSub2BaseCallCount copy() {
		return new BaseSub2BaseCallCount(this);
	}

	// TODO remove
	/*
	 * @Override public void init(final GeneralParameter parameter) throws
	 * InstantiationException, IllegalAccessException { for (final BaseSub baseSub :
	 * parameter.getReadTags()) { BaseCallCount bcc = valueClass.newInstance();
	 * bcc.init(parameter); map.put(baseSub, bcc); } }
	 */

}
