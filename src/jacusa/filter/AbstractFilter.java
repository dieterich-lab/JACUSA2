package jacusa.filter;

import lib.data.result.Result;

/**
 * Abstract class to mark results as potential false positive outcomes.
 */
abstract class AbstractFilter implements Filter {

	// unique identifier for a filter
	private final char id;
	// region that is required up- and downstream of current position
	// this might be needed when data are needed outside of a thread window
	private final int overhang;

	protected AbstractFilter(final char id) {
		this(id, 0);
	}

	protected AbstractFilter(final char c, final int overhang) {
		this.id = c;
		this.overhang = overhang;
	}

	@Override
	public final char getID() {
		return id;
	}

	@Override
	public int getOverhang() {
		return overhang;
	}

	@Override
	public void markResult(final int valueIndex, final Result result) {
		result.getFilterInfo(valueIndex).add(Character.toString(getID()));
	}

}
