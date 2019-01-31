package lib.data.storage.container;

import htsjdk.samtools.AlignmentBlock;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.recordextended.SAMRecordExtended;
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

	public void addRecordExtended(final SAMRecordExtended recordExtended) {
		for (final AlignmentBlock currentBlock : recordExtended.getSAMRecord().getAlignmentBlocks()) {

			final int refPos = currentBlock.getReferenceStart();	
			final int readPos = currentBlock.getReadStart() - 1;
			final int len = currentBlock.getLength();
			final WindowPositionGuard currentWinPosGuard = coordinateController.convert(refPos, readPos, len);

			if (currentWinPosGuard.isValid()) {
				refSegmentContainer.markCovered(
						currentWinPosGuard.getWindowPosition(), 
						currentWinPosGuard.getReadPosition(), 
						currentWinPosGuard.getLength(),
						recordExtended);
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
		final int winPos = coordinateController.getCoordinateTranslator().coordinate2windowPosition(coordinate);
		return getReferenceBase(winPos);
	}
	
	@Override
	public Base getReferenceBase(final int winPos) {
		return Base.valueOf(refSegmentContainer.getReference(winPos));
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public void close() {
		// nothing to be done
	}
	
}
