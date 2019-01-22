package jacusa.filter;

import lib.data.result.Result;

/**
 * Abstract class that finds and marks false positive variants.
 * 
 * @param <T>
 */
abstract class AbstractFilter implements Filter {

	// unique char char identifies a filter
	private final char c;
	// region that is required up- and downstream of current position
	private final int overhang;

	protected AbstractFilter(final char c) {
		this(c, 0);
	}

	protected AbstractFilter(final char c, final int overhang) {
		this.c = c;
		this.overhang = overhang;
	}

	@Override
	public final char getC() {
		return c;
	}

	@Override
	public int getOverhang() {
		return overhang;
	}

	@Override
	public void addInfo(final int valueIndex, final Result result) {
		result.getFilterInfo(valueIndex).add(Character.toString(getC()));
	}

}
