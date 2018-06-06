package lib.data.basecall.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.count.BaseCallCount;
import lib.util.Base;

public class MapBaseCallCount 
implements BaseCallCount {

	// container
	private Map<Base, Integer> baseCall;

	public MapBaseCallCount() {
		baseCall = new HashMap<Base, Integer>(2);
	}

	public MapBaseCallCount(final Map<Base, Integer> baseCall) {
		this.baseCall = new HashMap<Base, Integer>(baseCall);
	}

	public MapBaseCallCount(final MapBaseCallCount baseCallCount) {
		this.baseCall = new HashMap<Base, Integer>(baseCallCount.baseCall);
	}

	@Override
	public MapBaseCallCount copy() {
		return new MapBaseCallCount(this);
	}

	@Override
	public int getCoverage() {
		int coverage = 0;
		
		for (final int c : baseCall.values()) {
			coverage += c;
		}

		return coverage;
	}
	
	@Override
	public int getBaseCall(final Base base) {
		return baseCall.containsKey(base) ? baseCall.get(base) : 0;
	}

	@Override
	public void increment(final Base base) {
		final int count = getBaseCall(base);
		set(base, count + 1);
	}

	@Override
	public void clear() {
		baseCall.clear();
	}

	@Override
	public void add(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			add(base, baseCallCount);
		}
	}

	@Override
	public void set(final Base base, final int count) {
		baseCall.put(base, count);
	}

	@Override
	public void add(final Base base, final BaseCallCount baseQualCount) {
		add(base, base, baseQualCount);
	}

	@Override
	public void add(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		final int countDest = getBaseCall(dest);
		final int countSrc = baseCallCount.getBaseCall(src);
		set(dest, countDest + countSrc);
	}
	
	@Override
	public void substract(final Base base, final BaseCallCount baseCallCount) {
		substract(base, base, baseCallCount);
	}

	@Override
	public void substract(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		final int countDest = getBaseCall(dest);
		final int countSrc = baseCallCount.getBaseCall(src);
		set(dest, countDest - countSrc);
	}
	
	@Override
	public void substract(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
				substract(base, baseCallCount);
		}
	}
	
	@Override
	public void invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (baseCall.get(base) == 0 && baseCall.get(complement) == 0) {
				continue;
			}
			final int tmpCount = getBaseCall(base);
			baseCall.put(base, getBaseCall(complement)); 
			baseCall.put(complement, tmpCount);
		}
	}

	@Override
	public Set<Base> getAlleles() {
		// make this allele
		Set<Base> alleles = new HashSet<Base>(2);
		for (final Base base : baseCall.keySet()) {
			if (getBaseCall(base) > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		int baseIndex = 0;
		final int n = SequenceUtil.VALID_BASES_UPPER.length;
		sb.append("(");
		sb.append(Base.valueOf(baseIndex));
		++baseIndex;
		for (; baseIndex < n; ++baseIndex) {
			sb.append(", ");
			sb.append(Base.valueOf(baseIndex));
		}
		sb.append(") (");
		
		baseIndex = 0;
		sb.append(getBaseCall(Base.valueOf(baseIndex)));
		++baseIndex;
		for (; baseIndex < n; ++baseIndex) {
			sb.append(", ");
			sb.append(getBaseCall(Base.valueOf(baseIndex)));
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
