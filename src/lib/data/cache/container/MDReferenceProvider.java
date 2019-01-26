package lib.data.cache.container;

import htsjdk.samtools.AlignmentBlock;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.Base;
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
	public Base getReferenceBase(final Coordinate coordinate) {
		final int windowPosition = coordinateController.getCoordinateTranslator().coordinate2windowPosition(coordinate);
		return getReferenceBase(windowPosition);
	}
	
	@Override
	public Base getReferenceBase(final int windowPosition) {
		return Base.valueOf(refSegmentContainer.getReference(windowPosition));
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public void close() {
		// nothing to be done
	}
	
}
