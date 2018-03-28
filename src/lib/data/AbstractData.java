package lib.data;

import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.util.coordinate.Coordinate;

public abstract class AbstractData
implements HasCoordinate, HasLibraryType {

	private LIBRARY_TYPE libraryType;
	private Coordinate coordinate;

	public AbstractData(final AbstractData abstractData) {
		this.libraryType = abstractData.libraryType;
		coordinate = new Coordinate(abstractData.getCoordinate());
	}
	
	public AbstractData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		this.libraryType = libraryType;
		this.coordinate = new Coordinate(coordinate);
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

	public abstract void add(final AbstractData abstractData);
	public abstract AbstractData copy();
	public abstract String toString();
	
}
