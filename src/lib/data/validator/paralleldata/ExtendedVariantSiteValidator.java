package lib.data.validator.paralleldata;

import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

/**
 * TODO
 */
public class ExtendedVariantSiteValidator 
implements ParallelDataValidator {
	
	private final DataType<BaseCallCount> dataType;
	
	public ExtendedVariantSiteValidator(final DataType<BaseCallCount> dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer container = parallelData.getCombPooledData();
		final BaseCallCount bcc = container.get(dataType);
		final Set<Base> alleles = bcc.getAlleles();
		// more than one non-reference allele
		if (alleles.size() > 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		Base referenceBase = container.getAutoRefBase();
		if (SequenceUtil.isValidBase(referenceBase.getByte())) {
			// there has to be at least one non-reference base call in the data
			return (bcc.getCoverage() - bcc.getBaseCall(referenceBase)) > 0;
		}

		return false;
	}

}
