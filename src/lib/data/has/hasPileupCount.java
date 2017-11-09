package lib.data.has;

import lib.data.basecall.PileupCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface hasPileupCount 
extends hasCoordinate, hasCoverage, hasReferenceBase, hasBaseCallCount {
	
	PileupCount getPileupCount();
	void setPileupCount(final PileupCount pileupCount);

}
