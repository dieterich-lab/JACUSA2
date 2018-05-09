package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;
import lib.data.cache.region.RegionDataCache;

/**
 * 
 */
public class ProcessDeletionOperator extends AbstractProcessRecord {

	public ProcessDeletionOperator(final int distance, final RegionDataCache<?> regionDataCache) {
		super(distance, regionDataCache);
	}
	
	@Override
	public void processRecord(SAMRecordWrapper recordWrapper) {
		for (final int cigarElementWrapperIndex : recordWrapper.getDeletion()) {
			processDeletionOperator(cigarElementWrapperIndex, recordWrapper);
		}	
	}

	private void processDeletionOperator(final int cigarElementWrapperIndex, final SAMRecordWrapper recordWrapper) {
		final CigarElementWrapper cigarElementWrapper = recordWrapper.getCigarElementWrappers().get(cigarElementWrapperIndex);
		final Position position = cigarElementWrapper.getPosition();
		
		// add upstream
		final int upstreamMatch = Math.min(getDistance(), recordWrapper.getUpstreamMatch(cigarElementWrapperIndex));
		if (upstreamMatch > 0) {
			getRegionCache().addRegion(
					position.getReferencePosition() - upstreamMatch, 
					position.getReadPosition() - upstreamMatch, 
					upstreamMatch, 
					recordWrapper);
		}

		// add downstream
		final int downstreamMatch = Math.min(getDistance(), recordWrapper.getDownstreamMatch(cigarElementWrapperIndex));
		if (downstreamMatch > 0) {
			getRegionCache().addRegion(
					position.getReferencePosition() + cigarElementWrapper.getCigarElement().getLength(), 
					position.getReadPosition(), 
					downstreamMatch, 
					recordWrapper);
		}
	}

	
}