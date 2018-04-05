package lib.data;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lib.data.has.HasRTarrestCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class LRTarrestCount implements HasRTarrestCount {

	private final RTarrestCount rtArrestCount;

	private final Map<Integer, BaseCallCount> refPos2bc4arrest;
	private final Map<Integer, BaseCallCount> refPos2bc4through;

	private final Map<Integer, Byte> reference;
	
	public LRTarrestCount() {
		rtArrestCount 		= new RTarrestCount();

		refPos2bc4arrest 	= new TreeMap<Integer, BaseCallCount>();
		refPos2bc4through 	= new TreeMap<Integer, BaseCallCount>();
		
		reference			= new HashMap<Integer, Byte>();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param lrtArrestCount
	 */
	public LRTarrestCount(final LRTarrestCount lrtArrestCount) {
		rtArrestCount 		= lrtArrestCount.rtArrestCount.copy();

		refPos2bc4arrest 	= new TreeMap<Integer, BaseCallCount>(lrtArrestCount.refPos2bc4arrest);
		refPos2bc4through 	= new TreeMap<Integer, BaseCallCount>(lrtArrestCount.refPos2bc4through);
		
		reference			= new HashMap<Integer, Byte>(lrtArrestCount.reference);
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
			refPos2bc.put(referencePosition, new BaseCallCount());
		}

		refPos2bc.get(referencePosition).add(baseCallCount);		
	}
	
	public Map<Integer, BaseCallCount> getRefPos2bc4arrest() {
		return refPos2bc4arrest;
	}
	
	public Map<Integer, BaseCallCount> getRefPos2bc4through() {
		return refPos2bc4through;
	}
	
	public Map<Integer, Byte> getReference() {
		return reference;
	}
	
	public void add(final LRTarrestCount lrtArrestCount) {
		this.rtArrestCount.add(lrtArrestCount.rtArrestCount);
		
		copy(refPos2bc4arrest, lrtArrestCount.refPos2bc4arrest);
		copy(refPos2bc4through, lrtArrestCount.refPos2bc4through);
		
		copy2(reference, lrtArrestCount.reference);
	}
	
	/**
	 * Helper function
	 * 
	 * @param src
	 * @param dest
	 */
	private void copy(final Map<Integer, BaseCallCount> dest, final Map<Integer, BaseCallCount> src) {
		for (final int referencePosition : src.keySet()) {
			if (dest.containsKey(referencePosition)) {
				dest.get(referencePosition).add(src.get(referencePosition));
			} else {
				dest.put(referencePosition, src.get(referencePosition));
			}
		}
	}

	/**
	 * Helper function
	 * 
	 * @param src
	 * @param dest
	 */
	private void copy2(final Map<Integer, Byte> dest, final Map<Integer, Byte> src) {
		for (final int referencePosition : src.keySet()) {
			if (! dest.containsKey(referencePosition)) {
				dest.put(referencePosition, src.get(referencePosition));
			}
		}
	}
	
	public LRTarrestCount copy() {
		return new LRTarrestCount(this);
	}
	
}