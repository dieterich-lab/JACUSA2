package lib.util.coordinate.provider;


import java.io.Closeable;
import java.util.Iterator;

import lib.util.coordinate.Coordinate;

/**
 * 
 * 
 */
public interface CoordinateProvider extends Iterator<Coordinate>, Closeable { 

	// nothing needed
	public int getTotal();

}
