package lib.io;

import lib.data.AbstractData;
import lib.data.result.Result;

public abstract class AbstractResultFormat<T extends AbstractData, R extends Result<T>> 
implements ResultFormat<T, R>{

	private final char c;
	private final String desc;
	
	public AbstractResultFormat(final char c, final String desc) {
		this.c = c;
		this.desc = desc;
	}

	@Override
	public final char getC() {
		return c;
	}

	@Override
	public final String getDesc() {
		return desc;
	}

}
