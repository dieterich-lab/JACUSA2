package jacusa.data;

import jacusa.util.Coordinate;

/**
 * 
 * @author michael
 *
 * 
 */
public abstract class AbstractData
implements hasCoordinate, hasReferenceBase {

	private Coordinate coordinate;

	
	public AbstractData() {
		coordinate = new Coordinate();
	}

	public AbstractData(final AbstractData abstractData) {
		coordinate = new Coordinate(abstractData.getCoordinate());
	}
	
	public AbstractData(final Coordinate coordinate) {
		this.coordinate = new Coordinate(coordinate);
	}
		
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public abstract void add(final AbstractData abstractData);
	public abstract AbstractData copy();
	public abstract String toString();
	
}
