package jacusa.filter;

/**
 * This filters sites, where the ratio of observed count and count after 
 * filtering(remaining count) does not meet a predefined ratio.
 * e.g.:
 * Required minRatio = 0.5, 
 * observed count = 10, 
 * after filtering filteredCount = 2 (8 have been filtered)
 * => 2 / 10 < 0.5 -> site will get filtered
 */
public class FilterByRatio {

	private final double minRatio;
	
	public FilterByRatio(final double minRatio) {
		this.minRatio = minRatio;
	}
	
	/**
	 * Filters sites, based on count, filteredCount, and required minimal 
	 * filterRatio.
	 * 
	 * @param count				observed count
	 * @param filteredCount		count after filtering
	 * @return
	 * 
	 * Tested in @see jacusa.filter.FilterByRatioTest
	 */
	public boolean filter(final int count, final int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio;
	}

}
