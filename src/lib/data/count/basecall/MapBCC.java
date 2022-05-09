package lib.data.count.basecall;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lib.util.Base;

public class MapBCC extends AbstractBaseCallCount {

	private static final long serialVersionUID = 1L;

	// container
	private final Map<Base, Integer> baseCalls;

	public MapBCC() {
		baseCalls = new EnumMap<>(Base.class);
	}

	public MapBCC(final Map<Base, Integer> baseCalls) {
		if (baseCalls == null) {
			throw new IllegalArgumentException("baseCalls == null");
		}
		this.baseCalls = baseCalls;
	}

	@Override
	public MapBCC copy() {
		return new MapBCC(new EnumMap<>(baseCalls));
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
	public MapBCC increment(final Base base) {
		final int count = getBaseCall(base);
		set(base, count + 1);
		return this;
	}

	@Override
	public MapBCC clear() {
		baseCalls.clear();
		return this;
	}

	@Override
	public MapBCC add(final BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			add(base, bcc);
		}
		return this;
	}

	@Override
	public MapBCC set(final Base base, final int count) {
		baseCalls.put(base, count);
		return this;
	}

	@Override
	public MapBCC add(final Base base, final BaseCallCount baseQualCount) {
		add(base, base, baseQualCount);
		return this;
	}

	@Override
	public MapBCC add(final Base dest, final Base src, final BaseCallCount bcc) {
		final int countDest = getBaseCall(dest);
		final int countSrc = bcc.getBaseCall(src);
		set(dest, countDest + countSrc);
		return this;
	}

	@Override
	public MapBCC subtract(final Base base, final BaseCallCount bcc) {
		subtract(base, base, bcc);
		return this;
	}

	@Override
	public MapBCC subtract(final Base dest, final Base src, final BaseCallCount bcc) {
		final int countDest = getBaseCall(dest);
		final int countSrc = bcc.getBaseCall(src);
		set(dest, countDest - countSrc);
		return this;
	}

	@Override
	public MapBCC subtract(final BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			subtract(base, bcc);
		}
		return this;
	}

	@Override
	public MapBCC invert() {
		for (final Base base : new Base[] { Base.A, Base.C }) {
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
		Set<Base> alleles = new TreeSet<>();
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
	 * Parser
	 */

	public static class Parser extends BaseCallCount.AbstractParser {

		public Parser() {
			super();
		}

		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}

		@Override
		public MapBCC parse(String s) {
			final String[] cols = split(s);
			final Map<Base, Integer> baseCalls = new EnumMap<>(Base.class);
			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				final Base base = Base.valueOf(baseIndex);
				baseCalls.put(base, Integer.parseInt(cols[baseIndex]));
				if (baseCalls.get(base) < 0) {
					throw new IllegalArgumentException();
				}
			}
			return new MapBCC(baseCalls);
		}

	}

}
