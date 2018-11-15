package jacusa.filter;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * Abstract class that finds and marks false positive variants.
 * 
 * @param <T>
 */
public abstract class AbstractFilter {

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

	/**
	 * Return the unique char id of this filter.
	 * 
	 * @return unique char
	 */
	public final char getC() {
		return c;
	}
	
	/**
	 * Return true or false if this filter identifies a site as an false position variant.
	 * This method can only be called from within the filter. 
	 * 
	 * @param parallelData the data to investigate
	 * @return true if site was filtered, or false otherwise
	 */
	protected abstract boolean filter(ParallelData parallelData);

	/**
	 * Returns the region that this filter requires up- and downstream from current position.
	 * 
	 * @return the region that the filter requires 
	 */
	public int getOverhang() {
		return overhang;
	}

	/**
	 * This method applies the filter to the data (ParallelData) stored within result object and 
	 * adds info fields to the result object if the filter found any false positive variants.
	 * 
	 * @param result the Result object to investigate and populate
	 * @return true if filter found artefact, false otherwise
	 */
	public boolean applyFilter(final Result result) {
		// get data to investigate
		final ParallelData parallelData = result.getParellelData();
		// if filter finds artefact, add info to result and return true
		boolean filter = false;
		for (final int valueIndex : result.getValueIndex()) {
			if (filter(parallelData)) {
				addInfo(valueIndex, result);
				filter = true;
			}
		}

		return filter;
	}

	/**
	 * Adds unique id of filter to result object 
	 * 
	 * @param result object to be marked by this filter 
	 */
	public void addInfo(final int valueIndex, final Result result) {
		result.getFilterInfo(valueIndex).add(Character.toString(getC()));
	}

}
