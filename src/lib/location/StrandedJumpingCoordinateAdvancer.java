package lib.location;

import lib.util.coordinate.CoordinateController;
import lib.data.assembler.ConditionContainer;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;

public class StrandedJumpingCoordinateAdvancer 
implements CoordinateAdvancer {

	private static final int MAX_MISS_COUNT = 5;
	
	private final StrandedCoordinateAdvancer defaultAdvancer;

	private final CoordinateController coordinateController;
	private final ConditionContainer conditionContainer;
	
	private int missCounter;
	
	public StrandedJumpingCoordinateAdvancer(
			final CoordinateController coordinateController,
			final ConditionContainer conditionContainer) {
		
		defaultAdvancer = new StrandedCoordinateAdvancer(new OneCoordinate());

		this.coordinateController = coordinateController;
		this.conditionContainer = conditionContainer;
	}
	
	@Override
	public Coordinate getCurrentCoordinate() {
		return defaultAdvancer.getCurrentCoordinate();
	}

	@Override
	public void advance() {
		if (missCounter < MAX_MISS_COUNT) { // try to do a "simple" advance
			simpleAdvance();
		} else { // or try to jump to next position
			missCounter = 0;
			jumpingAdvance();
		}
	}
	
	
	private void simpleAdvance() {
		defaultAdvancer.advance();
		missCounter++;
	}
	
	private void jumpingAdvance() {
		final int referencePosition = getCurrentCoordinate().get1Position();
		final int windowPosition = coordinateController.getCoordinateTranslator()
				.reference2windowPosition(referencePosition);
		
		if (windowPosition < 0) {
			return;
		}

		int newWindowPosition = windowPosition;

		for (int conditionIndex = 0; conditionIndex < conditionContainer.getConditionSize(); conditionIndex++) {
			final int tmpNextPosition = getNextWindowPosition(windowPosition, conditionContainer.getReplicatContainer(conditionIndex));
			if (tmpNextPosition == -1) {
				// advance to the end
				getCurrentCoordinate().setMaxPosition();
				return;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
		
		if (newWindowPosition > windowPosition) {
			getCurrentCoordinate().setStrand(STRAND.FORWARD);
			getCurrentCoordinate().set1Position(coordinateController
					.getCoordinateTranslator().window2referencePosition(newWindowPosition));
		} else {
			getCurrentCoordinate().setMaxPosition();
		}
	}

	private int getNextWindowPosition(final int windowPosition, final ReplicateContainer replicateContainer) {
		int newWindowPosition = windowPosition;

		for (final DataAssembler dataAssembler : replicateContainer.getDataAssemblers()) {
			final int tmpNextPosition = dataAssembler.getCacheContainer().getNext(windowPosition);
			if (tmpNextPosition == -1) {
				return -1;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
	
		return newWindowPosition > windowPosition ? newWindowPosition : -1; 
	}
	
	@Override
	public void adjustPosition(final Coordinate coordinate) {
		// contig is set somewhere else
		getCurrentCoordinate().setPosition(coordinate);
		getCurrentCoordinate().setStrand(coordinate.getStrand());
	}
	
}
