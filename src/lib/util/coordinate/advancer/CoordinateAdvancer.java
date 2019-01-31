package lib.util.coordinate.advancer;

import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;

// has JUNIT tests
public interface CoordinateAdvancer {

	Coordinate getCurrentCoordinate();

	/**
	 * Tested in {@link test.lib.}
	 */
	void advance();
	void adjustPosition(final Coordinate newCoordinate);

	public static class Builder implements lib.util.Builder<CoordinateAdvancer> {
		
		private final LibraryType libraryType;
		
		private Coordinate coordinate;
		
		public Builder(final LibraryType libraryType) {
			this.libraryType = libraryType;
			
			// stores reference specific coordinates
			coordinate = new OneCoordinate();
		}
		
		public Builder withCoordinate(final Coordinate coordinate) {
			this.coordinate = coordinate.copy();
			// coordinate (start-end) may be 1-1 or 1-10 -> copy does not suffice
			// this.coordinate should be 1-1 there we assign
			this.coordinate.setPosition(coordinate);
			return this;
		}
		
		@Override
		public CoordinateAdvancer build() {
			switch (libraryType) {
			case UNSTRANDED:
				return new UnstrandedCoordinateAdvancer(coordinate);
				
			case RF_FIRSTSTRAND:
			case FR_SECONDSTRAND:
				return new StrandedCoordinateAdvancer(coordinate);
				
			default:
				throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
			}
		}
		
	}

}
