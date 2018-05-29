package lib.data.cache.region.isvalid;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.Base;

public interface BaseCallValidator {

	boolean isValid(int referencePosition, int windowPosition, int readPosition, 
			Base base, byte baseQuality,
			SAMRecord record);
	
}
