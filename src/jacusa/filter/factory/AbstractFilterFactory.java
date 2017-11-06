package jacusa.filter.factory;

import jacusa.data.AbstractData;
import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;

public abstract class AbstractFilterFactory<T extends AbstractData> {

	public final static char SEP = ':';

	private char c;
	protected String desc;

	public AbstractFilterFactory(final char c, final String desc) {
		this.c 				= c;
		this.desc 			= desc;
	}

	public char getC() {
		return c;
	}

	public String getDesc() {
		return desc;
	}

	public void processCLI(final String line) throws IllegalArgumentException {
		// implement to change behavior via CLI
	}

	public abstract void registerFilter(final FilterContainer<T> filterContainer);
	public abstract AbstractFilter<T> getFilter();

} 