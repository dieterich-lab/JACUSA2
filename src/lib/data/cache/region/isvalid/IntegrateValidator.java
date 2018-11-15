package lib.data.cache.region.isvalid;

import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;

public class IntegrateValidator implements BaseCallValidator {
		
		private final List<BaseCallValidator> validators;
		
		public IntegrateValidator(final List<BaseCallValidator> validators) {
			this.validators = validators;
		}
		
		@Override
		public boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
				final Base base, final byte baseQuality,
				final SAMRecord record) {
				
			for (final BaseCallValidator bcv : validators) {
				if (! bcv.isValid(referencePosition, windowPosition, readPosition, base, baseQuality, record)) {
					return false;
				}
			}
			return true;
		}

	}
