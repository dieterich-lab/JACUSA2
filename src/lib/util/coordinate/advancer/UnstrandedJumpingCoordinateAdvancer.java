package lib.util.coordinate.advancer;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.OneCoordinate;
import lib.data.assembler.DataAssembler;
import lib.util.ConditionContainer;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;

public class UnstrandedJumpingCoordinateAdvancer 
implements CoordinateAdvancer {

	private static final int MAX_MISS_COUNT = 5;
	
	private UnstrandedCoordinateAdvancer defaultAdvancer;
	
	private final CoordinateController coordinateController;
	private final ConditionContainer conditionContainer;
	
	private int missCounter;
	
	public UnstrandedJumpingCoordinateAdvancer( 
			final CoordinateController coordinateController,
			final ConditionContainer conditionContainer) {
		
		defaultAdvancer = new UnstrandedCoordinateAdvancer(new OneCoordinate());
		
		this.coordinateController 	= coordinateController;
		this.conditionContainer 	= conditionContainer;
		
		missCounter = 0;
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
		final int refPos = getCurrentCoordinate().get1Position();
		final int winPos = coordinateController.getCoordinateTranslator()
				.ref2winPos(refPos);
		
		if (winPos < 0) {
			return;
		}

		int newWindowPosition = winPos;

		for (int condI = 0; condI < conditionContainer.getConditionSize(); condI++) {
			final int tmpNextPosition = getNextWindowPosition(winPos, conditionContainer.getReplicatContainer(condI));
			if (tmpNextPosition == -1) {
				// advance to the end
				getCurrentCoordinate().setMaxPosition();
				return;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
		
		if (newWindowPosition > winPos) {
			getCurrentCoordinate().set1Position(coordinateController.getCoordinateTranslator()
					.win2refPos(newWindowPosition));
		} else {
			getCurrentCoordinate().setMaxPosition();
		}
	}

	private int getNextWindowPosition(final int winPos, final ReplicateContainer replicateContainer) {
		int newWindowPosition = winPos;

		for (final DataAssembler dataBuilder : replicateContainer.getDataAssemblers()) {
			final int tmpNextPosition = dataBuilder.getCacheContainer().getNextWindowPosition(winPos);
			if (tmpNextPosition == -1) {
				return -1;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
	
		return newWindowPosition > winPos ? newWindowPosition : -1; 
	}

	@Override
	public void adjustPosition(final Coordinate coordinate) {
		// contig is set somewhere else
		getCurrentCoordinate().setPosition(coordinate);
	}
	
}
