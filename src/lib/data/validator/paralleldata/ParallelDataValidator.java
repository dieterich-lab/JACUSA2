package lib.data.validator.paralleldata;

import lib.data.AbstractData;
import lib.data.ParallelData;

public interface ParallelDataValidator<T extends AbstractData>  {
	
	boolean isValid(final ParallelData<T> parallelData);
	
}