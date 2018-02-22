package lib.data;

import java.util.Map;
import java.util.TreeMap;

import lib.data.has.hasReadArrestCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class LinkedReadArrestCount implements hasReadArrestCount {

	private ReadArrestCount readArrestCount;

	private Map<Integer, BaseCallCount> refPos2baseChange4arrest;
	private Map<Integer, BaseCallCount> refPos2baseChange4through;

	public LinkedReadArrestCount() {
		readArrestCount = new ReadArrestCount();

		refPos2baseChange4arrest = new TreeMap<Integer, BaseCallCount>();
		refPos2baseChange4through = new TreeMap<Integer, BaseCallCount>();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param linkedReadArrestCount
	 */
	public LinkedReadArrestCount(final LinkedReadArrestCount linkedReadArrestCount) {
		readArrestCount = linkedReadArrestCount.readArrestCount.copy();

		refPos2baseChange4arrest = new TreeMap<Integer, BaseCallCount>(linkedReadArrestCount.refPos2baseChange4arrest);
		refPos2baseChange4through = new TreeMap<Integer, BaseCallCount>(linkedReadArrestCount.refPos2baseChange4through);
	}
	
	public ReadArrestCount getReadArrestCount() {
		return readArrestCount;
	}

	public void add2arrest(final int referencePosition, final int baseIndex) {
		if (! refPos2baseChange4arrest.containsKey(referencePosition)) {
			refPos2baseChange4arrest.put(referencePosition, new BaseCallCount());
		}

		refPos2baseChange4arrest.get(referencePosition).increment(baseIndex);
	}
	
	public void add2through(final int referencePosition, final int baseIndex) {
		if (! refPos2baseChange4through.containsKey(referencePosition)) {
			refPos2baseChange4through.put(referencePosition, new BaseCallCount());
		}

		refPos2baseChange4through.get(referencePosition).increment(baseIndex);
	}
	
	public Map<Integer, BaseCallCount> getRefPos2baseChange4arrest() {
		return refPos2baseChange4arrest;
	}
	
	public Map<Integer, BaseCallCount> getRefPos2baseChange4through() {
		return refPos2baseChange4through;
	}
	
	public void add(final LinkedReadArrestCount readInfoCount) {
		readArrestCount.add(readInfoCount.readArrestCount);
		
		copy(refPos2baseChange4arrest, readInfoCount.refPos2baseChange4arrest);
		copy(refPos2baseChange4through, readInfoCount.refPos2baseChange4through);
	}
	
	private void copy(final Map<Integer, BaseCallCount> src, final Map<Integer, BaseCallCount> dest) {
		for (final int referencePosition : src.keySet()) {
			if (dest.containsKey(referencePosition)) {
				dest.get(referencePosition).add(src.get(referencePosition));
			} else {
				dest.put(referencePosition, src.get(referencePosition));
			}
		}
	}

	public LinkedReadArrestCount copy() {
		return new LinkedReadArrestCount(this);
	}
	
}