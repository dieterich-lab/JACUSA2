package lib.data.count;

import lib.cli.options.filter.has.BaseSub;
import lib.data.IntegerData;

public class BaseSub2Integer extends AbstractMappedData<BaseSub2Integer, BaseSub, IntegerData> {

	private static final long serialVersionUID = 1L;

	public BaseSub2Integer() {
		super(BaseSub.class, IntegerData.class);
	}

	public BaseSub2Integer(BaseSub2Integer o) {
		super(o);
	}

	@Override
	public BaseSub2Integer copy() {
		return new BaseSub2Integer(this);
	}

	// TODO remove
	/*
	 * @Override public void init(final GeneralParameter parameter) throws
	 * InstantiationException, IllegalAccessException { for (final BaseSub baseSub :
	 * parameter.getReadTags()) { BaseCallCount bcc = valueClass.newInstance();
	 * bcc.init(parameter); map.put(baseSub, bcc); } }
	 */

}
