package lib.data.cache.region.isvalid;

import htsjdk.samtools.SAMRecord;
import lib.data.cache.region.VisitedReadPosition;
import lib.util.Base;

public class UniqueVisitBaseCallValidator 
implements BaseCallValidator {

	private final VisitedReadPosition visitedReadPosition;
	
	public UniqueVisitBaseCallValidator(final VisitedReadPosition visitedReadPosition) {
		this.visitedReadPosition = visitedReadPosition;
	}
	
	@Override
	public boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, 
			final byte baseQuality,
			final SAMRecord record) {
		return ! visitedReadPosition.isVisited(readPosition);
	}
	
}
