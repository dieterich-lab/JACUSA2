package lib.data.validator.paralleldata;

import lib.data.ParallelData;
import lib.util.Base;

public class KnownReferenceBase 
implements ParallelDataValidator {

	public KnownReferenceBase() {}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		return parallelData.getCombinedPooledData().getReferenceBase() != Base.N;
	}

}
