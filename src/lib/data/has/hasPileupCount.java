package lib.data.has;

import lib.data.PileupCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface hasPileupCount extends hasBaseCallCount, hasReferenceBase {
	
	PileupCount getPileupCount();

}
