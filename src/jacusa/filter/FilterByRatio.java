package jacusa.filter;

public class FilterByRatio {

	private final double minRatio;

	public FilterByRatio(final double minRatio) {
		this.minRatio = minRatio;
	}

	public boolean filter(final int count, final int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio;
	}

}
