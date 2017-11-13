package jacusa.data.validator;

import lib.data.AbstractData;
import lib.data.ParallelData;

public class DummyValidator<T extends AbstractData> 
implements ParallelDataValidator<T> {

	@Override
	public boolean isValid(ParallelData<T> parallelData) {
		return true;
	}

}
