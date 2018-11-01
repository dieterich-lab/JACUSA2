package lib.data.cache.region.isvalid;

import htsjdk.samtools.SAMRecord;
import lib.data.adder.IncrementAdder;
import lib.util.Base;

public class MaxDepthBaseCallValidator implements BaseCallValidator {

	private final int maxDepth;
	private final IncrementAdder baseCallDataAdder;
	
	public MaxDepthBaseCallValidator(final int maxDepth, final IncrementAdder baseCallDataAdder) {
		this.maxDepth 		= maxDepth;
		this.baseCallDataAdder = baseCallDataAdder;
	}
	
	@Override
	public boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, 
			final byte baseQuality,
			final SAMRecord record) {
		// ensure max depth
		return baseCallDataAdder.getCoverage(windowPosition) < maxDepth;
	}
	
}
