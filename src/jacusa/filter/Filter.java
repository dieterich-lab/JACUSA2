package jacusa.filter;

import lib.data.ParallelData;
import lib.data.result.Result;

public interface Filter {

	/**
	 * Return the unique char id of this filter.
	 * 
	 * @return unique char
	 */
	char getC();

	/**
	 * Returns the region that this filter requires up- and downstream from current position.
	 * 
	 * @return the region that the filter requires 
	 */
	int getOverhang();

	/**
	 * This method applies the filter to the data (ParallelData) stored within result object and 
	 * adds info fields to the result object if the filter found any false positive variants.
	 * 
	 * @param result the Result object to investigate and populate
	 * @return true if filter found artefact, false otherwise
	 * 
	 * Tested in @see test.test.jacusa.filter.AbstractFilterTest
	 */
	default boolean applyFilter(Result result) {
		// get data to investigate
		final ParallelData parallelData = result.getParellelData();
		// if filter finds potential false positive variant, mark result and return true
		boolean filter = false;
		for (final int valueIndex : result.getValuesIndex()) {
			if (filter(parallelData)) {
				markResult(valueIndex, result);
				filter = true;
			}
		}
		return filter;
	}

	/**
	 * Return true or false if this filter identifies a site as an false position variant.
	 * This method can only be called from within the filter. 
	 * 
	 * @param parallelData the data to investigate
	 * @return true if site was filtered, or false otherwise
	 */
	boolean filter(ParallelData parallelData);
	
	/**
	 * Adds unique id of filter to result object. Mark result a filtered. 
	 * 
	 * @param result object to be marked by this filter 
	 */
	void markResult(int valueIndex, Result result);

}