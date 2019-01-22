package lib.location;

import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;

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
			
			coordinate = new Coordinate();
		}
		
		public void withCoordinate(final Coordinate coordinate) {
			this.coordinate = coordinate;
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
