package jacusa.filter.storage;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;

public class DistanceStorage<F extends AbstractData & hasBaseCallCount> 
extends AbstractCacheStorage<F> 
implements ProcessRecord, ProcessInsertionOperator, ProcessDeletionOperator, ProcessSkippedOperator {

	private int distance;

	public DistanceStorage(final char c, final int distance, final Cache<F> cache) {
		super(c, cache);
		this.distance = distance;
	}

	/* TODo
	@Override
	public void processRecord(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		AlignmentBlock alignmentBlock;
		int windowPosition;

		// process read start and end
		List<AlignmentBlock> alignmentBlocks = record.getAlignmentBlocks();

		// read start
		alignmentBlock = alignmentBlocks.get(0);
		windowPosition = getBaseCallCache().getWindowCoordinates().convert2WindowPosition(alignmentBlock.getReferenceStart());
		addRegion(windowPosition, distance + 1, alignmentBlock.getReadStart() - 1, recordWrapper);
	
		// read end
		alignmentBlock = alignmentBlocks.get(alignmentBlocks.size() - 1); // get last alignment
		windowPosition = getBaseCallCache().getWindowCoordinates().convert2WindowPosition(alignmentBlock.getReferenceStart() + alignmentBlock.getLength() - 1 - distance);
		// note: alignmentBlock.getReadStart() is 1-indexed
		addRegion(windowPosition, distance + 1, alignmentBlock.getReadStart() - 1 + alignmentBlock.getLength() - 1 - distance, recordWrapper);
	}

	// process IN
	@Override
	public void processInsertionOperator(final Position position, final SAMRecordWrapper recordWrapper) {
 		int upstreamD = Math.min(distance, upstreamMatch);
		addRegion(windowPosition - upstreamD, upstreamD, readPosition - upstreamD, record);

		int downStreamD = Math.min(distance, downstreamMatch);
		addRegion(windowPosition, downStreamD + 1, readPosition + cigarElement.getLength(), record);
	}

	// process DELs
	@Override
	public void processDeletionOperator(int windowPosition, int readPosition, int genomicPosition, int upstreamMatch, int downstreamMatch, CigarElement cigarElement, SAMRecord record) {
		int upstreamD = Math.min(distance, upstreamMatch);
		addRegion(windowPosition - upstreamD, upstreamD + 1, readPosition - upstreamD, record);

		windowPosition = getBaseCallCache().getWindowCoordinates().convert2WindowPosition(genomicPosition + cigarElement.getLength());
		int downStreamD = Math.min(distance, downstreamMatch);
		addRegion(windowPosition, downStreamD + 1, readPosition, record);
	}

	// process SpliceSites
	@Override
	public void processSkippedOperator(final SAMRecordWrapper recordWrapper) {
		recordWrapper.getDistances(genomicPosition, wrappers);
		int upstreamD = Math.min(distance, upstreamMatch);
		addRegion(windowPosition - upstreamD, upstreamD + 1, readPosition - upstreamD, recordWrapper);
		
		int downStreamD = Math.min(distance, downstreamMatch);
		addRegion(windowPosition + cigarElement.getLength(), downStreamD + 1, readPosition, recordWrapper);
	}
	*/
	
	/**
	 * 
	 * @return
	 */
	public int getOverhang() {
		return distance;
	}

	@Override
	public void processSkippedOperator(Position position,
			SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDeletionOperator(Position position,
			SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processInsertionOperator(Position position,
			SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRecord(SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}

}