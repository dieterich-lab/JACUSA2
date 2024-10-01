package lib.util.coordinate.provider;


import java.io.Closeable;
import java.util.Iterator;

import lib.util.coordinate.Coordinate;

/**
 * TODO add documentation
 */
public interface CoordinateProvider extends Iterator<Coordinate>, Closeable { 

	public int getTotal();

}
