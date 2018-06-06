package lib.data.cache.region.isvalid;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;

public class MinBASQBaseCallValidator implements BaseCallValidator {

	private final byte minBASQ;
	
	public MinBASQBaseCallValidator(final byte minBASQ) {
		this.minBASQ = minBASQ;
	}

	@Override
	public boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {
			
		return baseQuality >= minBASQ;
	}

}
