package lib.data.cache;

import lib.data.AbstractData;

import lib.tmp.CoordinateController;

public abstract class AbstractCache<T extends AbstractData> 
implements Cache<T> {

	protected final CoordinateController coordinateController;
	
	public AbstractCache(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
	}
	
}
