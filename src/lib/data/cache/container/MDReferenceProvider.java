package lib.data.cache.container;

import htsjdk.samtools.AlignmentBlock;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

public class MDReferenceProvider implements ReferenceProvider {

	private final CoordinateController coordinateController;

	private final ReferenceSegmentContainer refSegmentContainer;
	private Coordinate window;
	
	public MDReferenceProvider(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
		
		refSegmentContainer = new ReferenceSegmentContainer(coordinateController.getActiveWindowSize());
	}

	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock currentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {

			final int refPos = currentBlock.getReferenceStart();	
			final int readPos = currentBlock.getReadStart() - 1;
			final int len = currentBlock.getLength();
			final WindowPositionGuard currentWinPosGuard = coordinateController.convert(refPos, readPos, len);

			if (currentWinPosGuard.isValid()) {
				refSegmentContainer.markCovered(
						currentWinPosGuard.getWindowPosition(), 
						currentWinPosGuard.getReadPosition(), 
						currentWinPosGuard.getLength(),
						recordWrapper);
			}
		}
	}

	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();
			refSegmentContainer.clear();
		}
	}
	
	@Override
	public byte getReference(final Coordinate coordinate) {
		final int windowPosition = coordinateController.convert2windowPosition(coordinate);
		return getReference(windowPosition);
	}
	
	@Override
	public byte getReference(final int windowPosition) {
		return refSegmentContainer.getReference(windowPosition);
	}
	
}
