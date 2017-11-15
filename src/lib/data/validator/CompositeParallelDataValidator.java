package lib.data.validator;

import java.util.List;

import lib.data.AbstractData;
import lib.data.ParallelData;

public class CompositeParallelDataValidator<T extends AbstractData> 
implements ParallelDataValidator<T> {

	private final List<ParallelDataValidator<T>> validators;
	
	public CompositeParallelDataValidator(final List<ParallelDataValidator<T>> validators) {
		this.validators = validators;
	}
	
	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		for (final ParallelDataValidator<T> validator : validators) {
			if (! validator.isValid(parallelData)) {
				return false;
			}
		}

		return true;
	}
	
}
