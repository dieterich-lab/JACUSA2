package lib.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ReadInfoExtendedCount {

	// container
	private int start;
	private int inner;
	private int end;

	private int arrest;
	private int through;

	private Map<Integer, BaseCallCount> refPos2baseChange4arrest;
	private Map<Integer, BaseCallCount> refPos2baseChange4through;
	
	public ReadInfoExtendedCount() {
		start 	= 0;
		inner 	= 0;
		end 	= 0;
		
		arrest	= 0;
		through = 0;

		refPos2baseChange4arrest = new TreeMap<Integer, BaseCallCount>();
		refPos2baseChange4through = new TreeMap<Integer, BaseCallCount>();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param readInfoExtendedCount
	 */
	public ReadInfoExtendedCount(final ReadInfoExtendedCount readInfoExtendedCount) {
		this.start 		= readInfoExtendedCount.start;
		this.inner 		= readInfoExtendedCount.inner;
		this.end 		= readInfoExtendedCount.end;
		
		this.arrest 	= readInfoExtendedCount.arrest;
		this.through	= readInfoExtendedCount.through;

		refPos2baseChange4arrest = new TreeMap<Integer, BaseCallCount>(readInfoExtendedCount.refPos2baseChange4arrest);
		refPos2baseChange4through = new TreeMap<Integer, BaseCallCount>(readInfoExtendedCount.refPos2baseChange4through);
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	public int getInner() {
		return inner;
	}

	public void setInner(final int inner) {
		this.inner = inner;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getArrest() {
		return arrest;
	}
	
	public void setArrest(final int arrest) {
		this.arrest = arrest;
	}
	
	public int getThrough() {
		return through;
	}
	
	public void setThrough(final int through) {
		this.through = through;
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
	
	public void add(final ReadInfoExtendedCount readInfoCount) {
		start 	+= readInfoCount.start;
		inner 	+= readInfoCount.inner;
		end 	+= readInfoCount.end;
		
		arrest	+= readInfoCount.arrest;
		through	+= readInfoCount.through;
		
		
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
	
	public ReadInfoExtendedCount copy() {
		return new ReadInfoExtendedCount(this);
	}
	
}