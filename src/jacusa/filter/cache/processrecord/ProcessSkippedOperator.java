package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.CombinedPosition;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.data.cache.region.RegionDataCache;

/**
 * TODO add comments.
 */
public class ProcessSkippedOperator extends AbstractProcessRecord {

	public ProcessSkippedOperator(final int distance, final RegionDataCache regionDataCache) {
		super(distance, regionDataCache);
	}

	@Override
	public void processRecord(SAMRecordWrapper recordWrapper) {
		for (final int cigarElementWrapperIndex : recordWrapper.getSkipped()) {
			processSkippedOperator(cigarElementWrapperIndex, recordWrapper);
		}
	}
	
	/**
	 * Helper method.
	 * 
	 * @param cigarElementWrapperIndex
	 * @param recordWrapper
	 */
	private void processSkippedOperator(final int cigarElementWrapperIndex, final SAMRecordWrapper recordWrapper) {
		final CigarElementWrapper cigarElementWrapper = 
				recordWrapper.getCigarElementWrappers().get(cigarElementWrapperIndex);
		final CombinedPosition position = cigarElementWrapper.getPosition();
		
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