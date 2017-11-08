package lib.util.coordinateprovider;


import java.io.Closeable;
import java.util.Iterator;

import lib.util.Coordinate;

/**
 * 
 * @author Michael Piechotta
 */
public interface CoordinateProvider extends Iterator<Coordinate>, Closeable { 

	// nothing needed
	public int getTotal();

}
