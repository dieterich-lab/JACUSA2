package lib.data.validator;

import lib.data.AbstractData;
import lib.data.has.hasCoverage;

public class MinCoverage<T extends AbstractData & hasCoverage> 
implements DataValidator<T> {

	private final int minCoverage;
	
	public MinCoverage(final int minCoverage) {
		this.minCoverage = minCoverage;
	}
	
	public boolean isValid(T data) {
		return data.getCoverage() >= minCoverage;
	}
	
}
