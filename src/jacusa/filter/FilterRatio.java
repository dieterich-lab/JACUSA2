package jacusa.filter;

public class FilterRatio {

	private final double minRatio;
	
	public FilterRatio(final double minRatio) {
		this.minRatio = minRatio;
	}
	
	public boolean filter(final int count, final int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio;
	}
	
}
