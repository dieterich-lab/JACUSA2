package lib.data.validator.data;

import lib.data.AbstractData;
import lib.data.has.HasCoverage;

public class MinCoverage<T extends AbstractData & HasCoverage> 
implements DataValidator<T> {

	private final int minCoverage;
	
	public MinCoverage(final int minCoverage) {
		this.minCoverage = minCoverage;
	}
	
	public boolean isValid(T data) {
		return data.getCoverage() >= minCoverage;
	}
	
}
