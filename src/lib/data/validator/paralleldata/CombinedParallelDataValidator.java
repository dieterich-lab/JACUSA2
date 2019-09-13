package lib.data.validator.paralleldata;

import java.util.List;

import lib.data.ParallelData;

public class CombinedParallelDataValidator 
implements ParallelDataValidator {

	private final List<ParallelDataValidator> validators;
	
	public CombinedParallelDataValidator(final List<ParallelDataValidator> validators) {
		this.validators = validators;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		for (final ParallelDataValidator validator : validators) {
			if (! validator.isValid(parallelData)) {
				return false;
			}
		}

		return true;
	}
	
}
