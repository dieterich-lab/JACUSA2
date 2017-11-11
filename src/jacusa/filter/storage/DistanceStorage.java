package jacusa.filter.storage;


import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

public class DistanceStorage<T extends AbstractData & hasBaseCallCount> 
extends AbstractCacheStorage<T> 
implements ProcessRecord, ProcessInsertionOperator, ProcessDeletionOperator, ProcessSkippedOperator {

	private int distance;

	public DistanceStorage(final char c, final int distance, final BaseCallConfig baseConfig, final int activeWindowSize) {
		super(c, baseConfig, activeWindowSize);
		this.distance = distance;
	}

	@Override
	public void processRecord(int genomicWindowStart, SAMRecord record) {
		AlignmentBlock alignmentBlock;
		int windowPosition;

		// process read start and end
		List<AlignmentBlock> alignmentBlocks = record.getAlignmentBlocks();

		// read start
		alignmentBlock = alignmentBlocks.get(0);
		windowPosition = getBaseCallCache().getWindowCoordinates().convert2WindowPosition(alignmentBlock.getReferenceStart());
		addRegion(windowPosition, distance + 1, alignmentBlock.getReadStart() - 1, record);
	
		// read end
		alignmentBlock = alignmentBlocks.get(alignmentBlocks.size() - 1); // get last alignment
		windowPosition = getBaseCallCache().getWindowCoordinates().convert2WindowPosition(alignmentBlock.getReferenceStart() + alignmentBlock.getLength() - 1 - distance);
		// note: alignmentBlock.getReadStart() is 1-indexed
		addRegion(windowPosition, distance + 1, alignmentBlock.getReadStart() - 1 + alignmentBlock.getLength() - 1 - distance, record);
	}

	// process IN
	@Override
	public void processInsertionOperator(int windowPosition, int readPosition, int genomicPosition, int upstreamMatch, int downstreamMatch, CigarElement cigarElement, SAMRecord record) {
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
	public void processSkippedOperator(int windowPosition, int readPosition, int genomicPosition, int upstreamMatch, int downstreamMatch, CigarElement cigarElement, SAMRecord record) {
		int upstreamD = Math.min(distance, upstreamMatch);
		addRegion(windowPosition - upstreamD, upstreamD + 1, readPosition - upstreamD, record);
		
		int downStreamD = Math.min(distance, downstreamMatch);
		addRegion(windowPosition + cigarElement.getLength(), downStreamD + 1, readPosition, record);
	}

	/**
	 * 
	 * @return
	 */
	public int getOverhang() {
		return distance;
	}

}