package jacusa.filter.cache;

import lib.data.AbstractData;

public abstract class AbstractFilterCache<F extends AbstractData> implements FilterCache<F> {

	// corresponds to CLI option 
	private final char c;
	
	public AbstractFilterCache(final char c) {
		this.c = c;
	}

	@Override
	public final char getC() {
		return c;
	}

}
