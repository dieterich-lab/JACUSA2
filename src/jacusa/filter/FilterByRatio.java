package jacusa.filter;

public class FilterByRatio {

	private final double minRatio;

	public FilterByRatio(final double minRatio) {
		this.minRatio = minRatio;
	}

	/**
	 * TODO add comment
	 * 
	 * @param count
	 * @param filteredCount
	 * @return
	 * 
	 * Tested in @see jacusa.filter.FilterByRatioTest
	 */
	public boolean filter(final int count, final int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio;
	}

}
