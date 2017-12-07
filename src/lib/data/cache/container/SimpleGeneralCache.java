package lib.data.cache.container;

import htsjdk.samtools.AlignmentBlock;

import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class SimpleGeneralCache implements GeneralCache {

	private final CoordinateController coordinateController;

	private final FileReferenceProvider referenceProvider;
	private final NextPositionSegmentContainer segmentContainer;
	
	public SimpleGeneralCache(final FileReferenceProvider referenceProvider, 
			final CoordinateController coordinateController) {
		
		this.coordinateController = coordinateController;
		
		this.referenceProvider = referenceProvider;
		segmentContainer = new NextPositionSegmentContainer(coordinateController.getActiveWindowSize());
	
		clear();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		final NextPositionSegment segment = segmentContainer.get(windowPosition);
		
		switch (segment.getType()) {
		
		case NOT_COVERED:
			return segment.getNext();
			
		case UNKNOWN:
			return segment.getNext();
			
		case COVERED:
			if (windowPosition + 1 >= segment.getEnd()) {
				return segment.getNext();
			}
			return windowPosition + 1;	

		default:
			throw new IllegalStateException("Unknown segment type: " + segment.getType());

		}
	}

	@Override
	public byte getReference(int windowPosition) {
		return referenceProvider.getReference(windowPosition);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		AlignmentBlock previousBlock = null;

		for (final AlignmentBlock currentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			
			final int currentRefPos = currentBlock.getReferenceStart();	
			final int currentReadPos = currentBlock.getReadStart() - 1;
			final int currentLen = currentBlock.getLength();
			final WindowPositionGuard currentWinPosGuard = coordinateController.convert(currentRefPos, currentReadPos, currentLen);
			
			if (currentWinPosGuard.isValid()) {
				segmentContainer.markCovered(currentWinPosGuard.getWindowPosition(), currentWinPosGuard.getLength());
			}
				
			if (previousBlock != null) {
				final int previousRefPos = previousBlock.getReferenceStart() + previousBlock.getLength();	
				final int previousLen = currentRefPos - 1 - previousRefPos;
				final WindowPositionGuard previousWinPosGuard = coordinateController.convert(previousRefPos, previousLen);
				
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
	
}
