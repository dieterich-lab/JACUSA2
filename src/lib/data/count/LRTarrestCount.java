package lib.data.count;

import java.util.Map;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.has.HasRTcount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class LRTarrestCount implements HasRTcount {

	private final RTarrestCount rtArrestCount;
	private final RefPos2BaseCallCount refPos2bc4arrest;
	
	public LRTarrestCount() {
		rtArrestCount 		= new RTarrestCount();

		refPos2bc4arrest 	= new RefPos2BaseCallCount();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param lrtArrestCount
	 */
	public LRTarrestCount(final LRTarrestCount lrtArrestCount) {
		this();
		add(lrtArrestCount);
	}
	
	public RTarrestCount getRTarrestCount() {
		return rtArrestCount;
	}
	
	/**
	 * Helper function
	 * 
	 * @param refPos2bc
	 * @param referencePosition
	 * @param baseIndex
	 */
	public void add(final Map<Integer, BaseCallCount> refPos2bc, final int referencePosition, final BaseCallCount baseCallCount) {
		if (! refPos2bc.containsKey(referencePosition)) {
			refPos2bc.put(referencePosition, new ArrayBaseCallCount());
		}

		refPos2bc.get(referencePosition).add(baseCallCount);		
	}
	
	public RefPos2BaseCallCount getRefPos2bc4arrest() {
		return refPos2bc4arrest;
	}
		
	public void add(final LRTarrestCount lrtArrestCount) {
		this.rtArrestCount.add(lrtArrestCount.rtArrestCount);
		
		this.refPos2bc4arrest.add(lrtArrestCount.refPos2bc4arrest);
	}
	
	public LRTarrestCount copy() {
		return new LRTarrestCount(this);
	}
	
}