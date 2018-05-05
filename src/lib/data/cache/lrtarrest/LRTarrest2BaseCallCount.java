package lib.data.cache.lrtarrest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.data.BaseCallCount;
import lib.util.coordinate.CoordinateController;

public class LRTarrest2BaseCallCount {

	private final CoordinateController coordinateController;

	private Map<Integer, Integer> arrest2count;
	private Map<Integer, Map<Integer, BaseCallCount>> arrest2ref2bc;

	public LRTarrest2BaseCallCount(CoordinateController coordinateController) {
		this.coordinateController = coordinateController;

		final int n = coordinateController.getActiveWindowSize();
		arrest2count		= new HashMap<Integer, Integer>(n);
		arrest2ref2bc		= new HashMap<Integer, Map<Integer,BaseCallCount>>();
	}

	public Map<Integer, BaseCallCount> getRef2bc(final int arrestPos) {
		return arrest2ref2bc.get(arrestPos);
	}
	
	public void addArrest(final int windowArrestPos) {
		if (! arrest2count.containsKey(windowArrestPos)) {
			arrest2count.put(windowArrestPos, 0);
		}
		final int tmp = arrest2count.get(windowArrestPos);
		arrest2count.put(windowArrestPos, tmp + 1);
	}
	
	public void addBaseCall(final int winArrestPos, final int refBCPos, final int baseIndex) {
		if (! arrest2ref2bc.containsKey(winArrestPos)) {
			arrest2ref2bc.put(winArrestPos, new HashMap<Integer, BaseCallCount>());
		}

		if (! arrest2ref2bc.get(winArrestPos).containsKey(refBCPos)) {
			arrest2ref2bc.get(winArrestPos).put(refBCPos, new BaseCallCount());
		}
		
		arrest2ref2bc.get(winArrestPos).get(refBCPos).increment(baseIndex);
	}

	public Set<Integer> getArrest() {
		return arrest2ref2bc.keySet();
	}

	public int getArrestCount(final int arrestPos) {
		if (! arrest2count.containsKey(arrestPos)) {
			return 0;
		}
		return arrest2count.get(arrestPos);
	}

	public void clear() {
		final int n = coordinateController.getActiveWindowSize();
		if (getArrest().size() < n) {
			arrest2count.clear();
			arrest2ref2bc.clear();
		} else {
			arrest2count	= new HashMap<Integer, Integer>(n);
			arrest2ref2bc 	= new HashMap<Integer, Map<Integer,BaseCallCount>>();
		}
	}

}
