package lib.data.cache.lrtarrest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.cli.options.Base;

public class ArrestPos2RefPos2BaseCallCount {

	private final int n;

	private int[] arrest2count;
	private Map<Integer, RefPos2BaseCallCount> arrest2ref2bc;

	public ArrestPos2RefPos2BaseCallCount(final int n) {
		this.n			= n;
		arrest2count	= new int[n];
		arrest2ref2bc	= new HashMap<Integer, RefPos2BaseCallCount>();
	}

	public RefPos2BaseCallCount getRef2bc(final int winArrestPos) {
		return arrest2ref2bc.get(winArrestPos);
	}

	public void addArrest(final int winArrestPos) {
		arrest2count[winArrestPos]++;
	}

	public void addBaseCall(final int winArrestPos, final int refBCPos, final Base base) {
		if (! arrest2ref2bc.containsKey(winArrestPos)) {
			arrest2ref2bc.put(winArrestPos, new RefPos2BaseCallCount());
		}

		final RefPos2BaseCallCount tmp = arrest2ref2bc.get(winArrestPos);
		tmp.addBaseCall(refBCPos, base);
	}

	public Set<Integer> getArrest() {
		return arrest2ref2bc.keySet();
	}

	public int getArrestCount(final int winArrestPos) {
		return arrest2count[winArrestPos];
	}

	public void clear() {
		Arrays.fill(arrest2count, 0);
		if (getArrest().size() < n) {
			arrest2ref2bc.clear();
		} else {
			arrest2ref2bc 	= new HashMap<Integer, RefPos2BaseCallCount>();
		}
	}

}
