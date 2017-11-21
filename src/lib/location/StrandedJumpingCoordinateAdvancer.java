package lib.location;

import lib.data.builder.ConditionContainer;
import lib.data.builder.DataBuilder;
import lib.data.builder.ReplicateContainer;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.Coordinate.STRAND;

public class StrandedJumpingCoordinateAdvancer 
implements CoordinateAdvancer {

	private final Coordinate coordinate;

	private final CoordinateController coordinateController;
	private final ConditionContainer<?> conditionContainer;
	
	public StrandedJumpingCoordinateAdvancer(
			final CoordinateController coordinateController,
			final ConditionContainer<?> conditionContainer) {
		coordinate = new Coordinate();

		this.coordinateController = coordinateController;
		this.conditionContainer = conditionContainer;
	}
	
	@Override
	public Coordinate getCurrentCoordinate() {
		return coordinate;
	}

	@Override
	public void advance() {
		_advance();
		final int referencePosition = coordinate.getPosition();
		final int windowPosition = coordinateController.convert2windowPosition(referencePosition);
		if (windowPosition < 0) {
			return;
		}

		int newWindowPosition = windowPosition;
		int check = 0;
		while (true) {
			for (int conditionIndex = 0; conditionIndex < conditionContainer.getConditionSize(); conditionIndex++) {
				final int tmp = getNextWindowPosition(newWindowPosition, conditionContainer.getReplicatContainer(conditionIndex));
				if (tmp == -1) {
					// advance to the end
					coordinate.setPosition(Integer.MAX_VALUE);
					return;
				}
				if (newWindowPosition == tmp) {
					check++;
					if (check == conditionContainer.getConditionSize()) {
						coordinate.setPosition(newWindowPosition);
						return;
					}
				} else {
					check = 0;
					newWindowPosition = tmp;
				}
			}
		}
	}

	private void _advance() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			coordinate.setStrand(STRAND.REVERSE);
		} else {
			coordinate.setStrand(STRAND.FORWARD);
			final int currentPosition = coordinate.getStart() + 1;
			coordinate.setPosition(currentPosition);
		}
	}
	
	private int getNextWindowPosition(final int windowPosition, final ReplicateContainer<?> replicateContainer) {
		int newWindowPosition = windowPosition;

		int check = 0;
		while (true) {
			check = 0;
			for (final DataBuilder<?> dataBuilder : replicateContainer.getDataBuilder()) {
				final int tmp = dataBuilder.getCacheContainer().getNext(newWindowPosition);
				if (tmp == -1) {
					return -1;
				}
				if (newWindowPosition == tmp) {
					check++;
					if (check == replicateContainer.getReplicateSize()) {
						return newWindowPosition;
					}
				} else {
					check = 0;
					newWindowPosition = tmp;
				}
			}
		}
	}

	@Override
	public void adjust(final Coordinate coordinate) {
		// contig is set somewhere else
		this.coordinate.setPosition(coordinate.getPosition());
		this.coordinate.setStrand(coordinate.getStrand());
	}
	
}
