package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;

public class NonHomozygousSite 
implements ParallelDataValidator {

	private final DataType<BaseCallCount> dataType;
	
	public NonHomozygousSite(final DataType<BaseCallCount> dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer container = parallelData.getCombPooledData();
		final BaseCallCount bcc = container.get(dataType);
		// more than one non-reference allele
		return bcc.getAlleles().size() > 1;
	}

}
