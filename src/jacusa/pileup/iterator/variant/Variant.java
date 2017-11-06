package jacusa.pileup.iterator.variant;

import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface Variant<T extends AbstractData>  {
	
	boolean isValid(final ParallelPileupData<T> parallelData);
	
}
