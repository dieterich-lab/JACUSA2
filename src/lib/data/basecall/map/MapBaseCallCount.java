package lib.data.basecall.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.count.BaseCallCount;

public class MapBaseCallCount 
implements BaseCallCount {

	// container
	private Map<Integer, Integer> baseCall;

	public MapBaseCallCount() {
		baseCall = new HashMap<Integer, Integer>(2);
	}

	public MapBaseCallCount(final Map<Integer, Integer> baseCall) {
		this.baseCall = new HashMap<Integer, Integer>(baseCall);
	}

	public MapBaseCallCount(final MapBaseCallCount baseCallCount) {
		this.baseCall = new HashMap<Integer, Integer>(baseCallCount.baseCall);
	}

	public MapBaseCallCount copy() {
		return new MapBaseCallCount(this);
	}

	public int getCoverage() {
		int coverage = 0;
		
		for (final int c : baseCall.values()) {
			coverage += c;
		}

		return coverage;
	}
	
	@Override
	public int getBaseCall(final int baseIndex) {
		return baseCall.containsKey(baseIndex) ? baseCall.get(baseIndex) : 0;
	}

	@Override
	public void increment(final int baseIndex) {
		final int count = getBaseCall(baseIndex);
		set(baseIndex, count + 1);
	}

	@Override
	public void clear() {
		baseCall.clear();
	}

	@Override
	public void add(final BaseCallCount baseCallCount) {
		for (final int baseIndex : baseCallCount.getAlleles()) {
			add(baseIndex, baseCallCount);
		}
	}

	@Override
	public void set(final int baseIndex, final int count) {
		baseCall.put(baseIndex, count);
	}

	@Override
	public void add(final int baseIndex, final BaseCallCount baseQualCount) {
		add(baseIndex, baseIndex, baseQualCount);
	}

	@Override
	public void add(final int baseIndexDest, final int baseIndexSrc, final BaseCallCount src) {
		final int countDest = getBaseCall(baseIndexDest);
		final int countSrc = getBaseCall(baseIndexSrc);
		set(baseIndexDest, countDest + countSrc);
	}
	
	@Override
	public void substract(final int baseIndex, final BaseCallCount src) {
		substract(baseIndex, baseIndex, src);
	}

	@Override
	public void substract(final int baseIndexDest, final int baseIndexSrc, final BaseCallCount src) {
		final int countDest = getBaseCall(baseIndexDest);
		final int countSrc = getBaseCall(baseIndexSrc);
		set(baseIndexDest, countDest - countSrc);
	}
	
	@Override
	public void substract(final BaseCallCount src) {
		for (final int baseIndex : src.getAlleles()) {
				substract(baseIndex, src);
		}
	}
	
	@Override
	public void invert() {
		final Map<Integer, Integer> tmp = new HashMap<Integer, Integer>(baseCall.size());
		for (final int baseIndex : getAlleles()) {
			final int complementaryBaseIndex 	= BaseCallConfig.BASES.length - baseIndex - 1;  
			tmp.put(complementaryBaseIndex, getBaseCall(baseIndex));
			tmp.put(baseIndex, getBaseCall(complementaryBaseIndex));
		}
		baseCall = tmp;
	}

	@Override
	public Set<Integer> getAlleles() {
		// make this allele
		Set<Integer> alleles = new HashSet<Integer>(2);
		for (int baseIndex = 0; baseIndex < BaseCallConfig.BASES.length; ++baseIndex) {
			if (getBaseCall(baseIndex) > 0) {
				alleles.add(baseIndex);
			}
		}
		return alleles;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		int baseIndex = 0;
		final int n = BaseCallConfig.BASES.length;
		sb.append("(");
		sb.append(BaseCallConfig.BASES[baseIndex]);
		++baseIndex;
		for (; baseIndex < n; ++baseIndex) {
			sb.append(", ");
			sb.append(BaseCallConfig.BASES[baseIndex]);
		}
		sb.append(") (");
		
		baseIndex = 0;
		sb.append(getBaseCall(baseIndex));
		++baseIndex;
		for (; baseIndex < n; ++baseIndex) {
			sb.append(", ");
			sb.append(getBaseCall(baseIndex));
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
