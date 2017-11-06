package jacusa.util.coordinateprovider;

import jacusa.util.Coordinate;

import java.io.Closeable;
import java.util.Iterator;

/**
 * 
 * @author Michael Piechotta
 */
public interface CoordinateProvider extends Iterator<Coordinate>, Closeable { 

	// nothing needed
	public int getTotal();

}
