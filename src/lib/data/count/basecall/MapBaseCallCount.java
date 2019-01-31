package lib.data.count.basecall;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lib.util.Base;

public class MapBaseCallCount extends AbstractBaseCallCount {

	private static final long serialVersionUID = 1L;
	
	// container
	private final Map<Base, Integer> baseCalls;

	public MapBaseCallCount() {
		baseCalls = new HashMap<>(2);
	}
	
	public MapBaseCallCount(final Map<Base, Integer> baseCalls) {
		if (baseCalls == null) {
			throw new IllegalArgumentException("baseCalls == null");
		}
		this.baseCalls = baseCalls;
	}
	
	@Override
	public MapBaseCallCount copy() {
		return new MapBaseCallCount(new HashMap<>(baseCalls));
	}

	@Override
	public int getCoverage() {
		int coverage = 0;
		for (final int c : baseCalls.values()) {
			coverage += c;
		}
		return coverage;
	}
	
	@Override
	public int getBaseCall(final Base base) {
		return baseCalls.containsKey(base) ? baseCalls.get(base) : 0;
	}

	@Override
	public MapBaseCallCount increment(final Base base) {
		final int count = getBaseCall(base);
		set(base, count + 1);
		return this;
	}

	@Override
	public MapBaseCallCount clear() {
		baseCalls.clear();
		return this;
	}

	@Override
	public MapBaseCallCount add(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			add(base, baseCallCount);
		}
		return this;
	}

	@Override
	public MapBaseCallCount set(final Base base, final int count) {
		baseCalls.put(base, count);
		return this;
	}

	@Override
	public MapBaseCallCount add(final Base base, final BaseCallCount baseQualCount) {
		add(base, base, baseQualCount);
		return this;
	}

	@Override
	public MapBaseCallCount add(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		final int countDest = getBaseCall(dest);
		final int countSrc = baseCallCount.getBaseCall(src);
		set(dest, countDest + countSrc);
		return this;
	}
	
	@Override
	public MapBaseCallCount subtract(final Base base, final BaseCallCount baseCallCount) {
		subtract(base, base, baseCallCount);
		return this;
	}

	@Override
	public MapBaseCallCount subtract(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		final int countDest = getBaseCall(dest);
		final int countSrc = baseCallCount.getBaseCall(src);
		set(dest, countDest - countSrc);
		return this;
	}
	
	@Override
	public MapBaseCallCount subtract(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			subtract(base, baseCallCount);
		}
		return this;
	}
	
	@Override
	public MapBaseCallCount invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (getBaseCall(base) == 0 && getBaseCall(complement) == 0) {
				continue;
			}
			final int tmpCount = getBaseCall(base);
			baseCalls.put(base, getBaseCall(complement)); 
			baseCalls.put(complement, tmpCount);
		}
		return this;
	}

	@Override
	public Set<Base> getAlleles() {
		// make this allele
		Set<Base> alleles = new TreeSet<Base>();
		for (final Base base : baseCalls.keySet()) {
			if (getBaseCall(base) > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}

	@Override
	public String toString() {
		return BaseCallCount.toString(this);
	}
	
	/*
	 * Factory and Parser
	 */

	public static class Factory extends BaseCallCountFactory<MapBaseCallCount> {
		
		@Override
		public MapBaseCallCount create() {
			return new MapBaseCallCount();
		}
		
	}
	
	public static class Parser extends BaseCallCount.AbstractParser {

		public Parser() {
			super();
		}
		
		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}
		
		@Override
		public MapBaseCallCount parse(String s) {
			final String[] cols = split(s);
			final Map<Base, Integer> baseCalls = new HashMap<>(cols.length);
			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				final Base base = Base.valueOf(baseIndex);
				baseCalls.put(base, Integer.parseInt(cols[baseIndex]));
			}
			return new MapBaseCallCount(baseCalls);
		}
		
	}
	
}
