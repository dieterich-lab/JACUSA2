package lib.data.validator.paralleldata;

import lib.data.ParallelData;

public class DummyValidator 
implements ParallelDataValidator {

	@Override
	public boolean isValid(final ParallelData parallelData) {
		return true;
	}

}
