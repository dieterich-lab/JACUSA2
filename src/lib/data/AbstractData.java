package lib.data;

import lib.data.has.hasCoordinate;
import lib.data.has.hasLibraryType;
import lib.util.Coordinate;

public abstract class AbstractData
implements hasCoordinate, hasLibraryType {

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

	public AbstractData() {
		this(new Coordinate());
	}
	
	public AbstractData(final Coordinate coordinate) {
		this(LIBRARY_TYPE.UNSTRANDED, coordinate);
	}
		
	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public abstract void add(final AbstractData abstractData);
	public abstract AbstractData copy();
	public abstract String toString();
	
}
