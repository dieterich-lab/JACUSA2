package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;
import lib.data.cache.region.UniqueRegionDataCache;

/**
 * TODO add comments.
 */
public class ProcessSkippedOperator extends AbstractProcessRecord {

	public ProcessSkippedOperator(final int distance, final UniqueRegionDataCache<?> uniqueDataCache) {
		super(distance, uniqueDataCache);
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
	// FIXME
	private void processSkippedOperator(final int cigarElementWrapperIndex, final SAMRecordWrapper recordWrapper) {
		final CigarElementWrapper cigarElementWrapper = 
				recordWrapper.getCigarElementWrappers().get(cigarElementWrapperIndex);
		final Position position = cigarElementWrapper.getPosition();
		
		// add upstream
		final int upstreamMatch = Math.min(getDistance(), recordWrapper.getUpstreamMatch(cigarElementWrapperIndex));
		// mark region
		getUniqueCache().addRecordWrapperRegion(
				position.getReferencePosition() - upstreamMatch,
				position.getReadPosition() - upstreamMatch, 
				upstreamMatch, 
				recordWrapper);

		// add downstream
		final int downstreamMatch = Math.min(getDistance(), recordWrapper.getDownstreamMatch(cigarElementWrapperIndex));
		// mark region
		getUniqueCache().addRecordWrapperRegion(
				position.getReferencePosition() + cigarElementWrapper.getCigarElement().getLength(),
				position.getReadPosition(), 
				downstreamMatch, 
				recordWrapper);		
	}

}