package lib.data.has;

import lib.data.PileupCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface HasPileupCount extends HasBaseCallCount, HasReferenceBase {
	
	PileupCount getPileupCount();

}
