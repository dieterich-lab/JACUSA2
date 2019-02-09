package lib.data.storage.container;

import htsjdk.samtools.AlignmentBlock;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.recordextended.SAMRecordExtended;

public class ComplexSharedStorage implements SharedStorage {

	private final ReferenceProvider referenceProvider;
	private final NextPositionSegmentContainer segmentContainer;
	
	public ComplexSharedStorage(
			final ReferenceProvider referenceProvider) {

		this.referenceProvider 	= referenceProvider;
		segmentContainer 		= new NextPositionSegmentContainer(
				getCoordinateController().getActiveWindowSize());
	}
	
	@Override
	public int getNext(final int winPos) {
		final NextPositionSegment segment = segmentContainer.get(winPos);
		
		switch (segment.getType()) {
		
		case NOT_COVERED:
			return segment.getNext();
			
		case UNKNOWN:
			return segment.getNext();
			
		case COVERED:
			if (winPos + 1 >= segment.getEnd()) {
				return segment.getNext();
			}
			return winPos + 1;	

		default:
			throw new IllegalStateException("Unknown segment type: " + segment.getType());

		}
	}

	@Override
	public void addRecordExtended(final SAMRecordExtended recordExtended) {
		referenceProvider.addRecordExtended(recordExtended);

		AlignmentBlock previousBlock = null;
		for (final AlignmentBlock currentBlock : recordExtended.getSAMRecord().getAlignmentBlocks()) {
			
			final int currentRefPos 	= currentBlock.getReferenceStart();	
			final int currentReadPos 	= currentBlock.getReadStart() - 1;
			final int currentLen 		= currentBlock.getLength();
			final WindowPositionGuard currentWinPosGuard = getCoordinateController().convert(currentRefPos, currentReadPos, currentLen);
			
			if (currentWinPosGuard.isValid()) {
				segmentContainer.markCovered(currentWinPosGuard.getWindowPosition(), currentWinPosGuard.getLength());
			}
				
			if (previousBlock != null) {
				final int previousRefPos = previousBlock.getReferenceStart() + previousBlock.getLength();	
				// final int previousLen = currentRefPos - 1 - previousRefPos;
				final int previousLen = currentRefPos - previousRefPos;
				final WindowPositionGuard previousWinPosGuard = getCoordinateController().convert(previousRefPos, previousLen);
				
				if (previousWinPosGuard.isValid()) {
					segmentContainer.markNotCovered(previousWinPosGuard.getWindowPosition(), 
							previousWinPosGuard.getLength(), currentWinPosGuard.getWindowPosition());
				}
			}
			previousBlock = currentBlock;
		}
	}

	@Override
	public void clear() {
		segmentContainer.clear();
		referenceProvider.update();
	}

	@Override
	public ReferenceProvider getReferenceProvider() {
		return referenceProvider;
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return referenceProvider.getCoordinateController();
	}
}
