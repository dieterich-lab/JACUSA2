package lib.location;

import lib.data.builder.ConditionContainer;
import lib.data.builder.DataBuilder;
import lib.data.builder.ReplicateContainer;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class StrandedJumpingCoordinateAdvancer 
implements CoordinateAdvancer {

	private static final int MAX_MISS_COUNT = 5;
	
	private final StrandedCoordinateAdvancer defaultAdvancer;

	private final CoordinateController coordinateController;
	private final ConditionContainer<?> conditionContainer;
	
	private int missCounter;
	
	public StrandedJumpingCoordinateAdvancer(
			final CoordinateController coordinateController,
			final ConditionContainer<?> conditionContainer) {
		
		defaultAdvancer = new StrandedCoordinateAdvancer(new Coordinate());

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
		final int referencePosition = getCurrentCoordinate().getPosition();
		final int windowPosition = coordinateController.convert2windowPosition(referencePosition);
		
		if (windowPosition < 0) {
			return;
		}

		int newWindowPosition = windowPosition;

		for (int conditionIndex = 0; conditionIndex < conditionContainer.getConditionSize(); conditionIndex++) {
			final int tmpNextPosition = getNextWindowPosition(windowPosition, conditionContainer.getReplicatContainer(conditionIndex));
			if (tmpNextPosition == -1) {
				// advance to the end
				getCurrentCoordinate().setPosition(Integer.MAX_VALUE);
				return;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
		
		if (newWindowPosition > windowPosition) {
			getCurrentCoordinate().setStrand(STRAND.FORWARD);
			getCurrentCoordinate().setPosition(coordinateController.convert2referencePosition(newWindowPosition));
		} else {
			getCurrentCoordinate().setPosition(Integer.MAX_VALUE);
		}
	}

	private int getNextWindowPosition(final int windowPosition, final ReplicateContainer<?> replicateContainer) {
		int newWindowPosition = windowPosition;

		for (final DataBuilder<?> dataBuilder : replicateContainer.getDataBuilder()) {
			final int tmpNextPosition = dataBuilder.getCacheContainer().getNext(windowPosition);
			if (tmpNextPosition == -1) {
				return -1;
			}
			newWindowPosition = Math.max(newWindowPosition, tmpNextPosition);
		}
	
		return newWindowPosition > windowPosition ? newWindowPosition : -1; 
	}
	
	@Override
	public void adjust(final Coordinate coordinate) {
		// contig is set somewhere else
		getCurrentCoordinate().setPosition(coordinate.getPosition());
		getCurrentCoordinate().setStrand(coordinate.getStrand());
	}
	
}
