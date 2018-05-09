package lib.data.has;

import lib.data.count.PileupCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface HasPileupCount extends HasBaseCallCount, HasReferenceBase {
	
	PileupCount getPileupCount();
	void setPileupCount(PileupCount pileupCount);

}
