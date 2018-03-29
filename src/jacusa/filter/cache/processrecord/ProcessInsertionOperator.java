package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;
import lib.data.cache.region.UniqueRegionDataCache;

// FIXME test
/**
 * 
 */
public class ProcessInsertionOperator extends AbstractProcessRecord {

	public ProcessInsertionOperator(final int distance, final UniqueRegionDataCache<?> uniqueDataCache) {
		super(distance, uniqueDataCache);
	}
	
	@Override
	public void processRecord(SAMRecordWrapper recordWrapper) {
		for (final int cigarElementWrapperIndex : recordWrapper.getInsertion()) {
			processInsertionOperator(cigarElementWrapperIndex, recordWrapper);
		}
	}
	
	private void processInsertionOperator(final int cigarElementWrapperIndex, final SAMRecordWrapper recordWrapper) {
		final CigarElementWrapper cigarElementWrapper = recordWrapper.getCigarElementWrappers().get(cigarElementWrapperIndex);
		final Position position = cigarElementWrapper.getPosition();
		
		// add upstream
		// FIXME
		final int upstreamMatch = Math.min(getDistance(), recordWrapper.getUpstreamMatch(cigarElementWrapperIndex));
		getUniqueCache().addRecordWrapperRegion(
				position.getReferencePosition() - upstreamMatch - 1,
				position.getReadPosition() - upstreamMatch, 
				upstreamMatch + 1, 
				recordWrapper);

		// add downstream
		final int downstreamMatch = Math.min(getDistance(), recordWrapper.getDownstreamMatch(cigarElementWrapperIndex));
		getUniqueCache().addRecordWrapperRegion(
				position.getReferencePosition(),
				position.getReadPosition() + cigarElementWrapper.getCigarElement().getLength(), 
				downstreamMatch, 
				recordWrapper);
	}	

}