package jacusa.pileup.iterator.variant;

import lib.data.AbstractData;
import lib.data.ParallelData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface Variant<T extends AbstractData>  {
	
	boolean isValid(final ParallelData<T> parallelData);
	
}
