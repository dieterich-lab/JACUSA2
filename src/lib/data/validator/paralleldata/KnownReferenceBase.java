package lib.data.validator.paralleldata;

import lib.data.ParallelData;
import lib.util.Base;

public class KnownReferenceBase 
implements ParallelDataValidator {

	public KnownReferenceBase() {
		// nothing needed
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final Base refBase = 
				parallelData.getCombPooledData().getAutoReferenceBase();
		return refBase != Base.N;
	}

}
