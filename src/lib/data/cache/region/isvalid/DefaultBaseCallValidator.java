package lib.data.cache.region.isvalid;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.SequenceUtil;
import lib.util.Base;

public class DefaultBaseCallValidator implements BaseCallValidator {


	@Override
	public boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {
			
		return SequenceUtil.isValidBase(base.getC());
	}

}
