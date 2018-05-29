package lib.data.adder;

import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractDataAdder<T extends AbstractData>
implements DataAdder<T> {
	
	private final CoordinateController coordinateController;
	
	public AbstractDataAdder(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
	}
	
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
}
