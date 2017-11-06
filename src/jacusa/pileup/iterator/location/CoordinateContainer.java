package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;

public class CoordinateContainer {

	// TODO add caches
	
	private CoordinateAdvancer[] coordinateAdvancers;

	public CoordinateContainer(final CoordinateAdvancer[] coordinateAdvancers) {
		this.coordinateAdvancers = coordinateAdvancers;
	}

	public void adjustCoordinate(final Coordinate nextCoordinate) {
		for (final CoordinateAdvancer advancer : coordinateAdvancers) {
			advancer.adjustPosition(nextCoordinate.getPosition(), nextCoordinate.getStrand());
		}
	}

	public Coordinate getCoordinate(final int i) {
		return coordinateAdvancers[i].getCurrentCoordinate();
	}

}

