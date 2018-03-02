package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.CigarElementWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;
import lib.data.cache.AbstractUniqueDataCache;

public class ProcessDeletionOperator extends AbstractProcessRecord {

	
	public ProcessDeletionOperator(final int distance, final AbstractUniqueDataCache<?> uniqueDataCache) {
		super(distance, uniqueDataCache);
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
		getCache().addRecordWrapperRegion(position.getReadPosition() - upstreamMatch, upstreamMatch + 1, recordWrapper);
		
		// add downstream
		final int downstreamMatch = Math.min(getDistance(), recordWrapper.getDownstreamMatch(cigarElementWrapperIndex));
		getCache().addRecordWrapperRegion(position.getReadPosition() + cigarElementWrapper.getCigarElement().getLength(), downstreamMatch + 1, recordWrapper);
	}

	
}