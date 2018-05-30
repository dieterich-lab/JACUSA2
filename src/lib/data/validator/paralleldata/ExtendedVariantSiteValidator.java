package lib.data.validator.paralleldata;

import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class ExtendedVariantSiteValidator<T extends AbstractData & HasBaseCallCount & HasReferenceBase> 
implements ParallelDataValidator<T> {
	
	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		final T data = parallelData.getCombinedPooledData();
		final Set<Base> alleles = data.getBaseCallCount().getAlleles();
		// more than one non-reference allele
		if (alleles.size() > 1) {
			return true;
		}

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		Base referenceBase = Base.valueOf(data.getReferenceBase());
		if (SequenceUtil.isValidBase(referenceBase.getC())) {
			
			if (parallelData.getCoordinate().getStrand() == STRAND.REVERSE) {
				referenceBase = referenceBase.getComplement();
			}

			// there has to be at least one non-reference base call in the data
			return data.getBaseCallCount().getCoverage() - data.getBaseCallCount().getBaseCall(referenceBase) > 0;
		}

		return false;
	}

}
