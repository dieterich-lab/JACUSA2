package lib.variant;

import lib.data.has.hasCoordinate;
import lib.util.coordinate.Coordinate;

public class Variant implements hasCoordinate {

	private Coordinate coordinate;
	
	private String reference;
	private String alternative;
	
	public Variant() {
		coordinate = new Coordinate();
		
		reference = new String();
		alternative = new String();
	}

	public Variant(final Coordinate coordinate, 
			final String reference, final String alternative) {
		this();
		this.coordinate = coordinate;
		this.reference = reference;
		this.alternative = alternative;
	}

	/**
	 * @return the base
	 */
	public String getAlternative() {
		return alternative;
	}

	public String getReference() {
		return reference;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
}
