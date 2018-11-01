package lib.data.count.basecall;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lib.util.Base;

public class ArrayBaseCallCount implements BaseCallCount {

	private static final long serialVersionUID = 1L;

	// container
	private final int[] baseCalls;

	public ArrayBaseCallCount() {
		this.baseCalls = new int[Base.validValues().length];
	}

	public ArrayBaseCallCount(final int[] baseCalls) {
		if (baseCalls.length != Base.validValues().length) {
			throw new IllegalArgumentException("baseCalls != Base.validValues().length");
		}
		this.baseCalls = baseCalls;
	}

	@Override
	public ArrayBaseCallCount copy() {
		return new ArrayBaseCallCount(Arrays.copyOf(baseCalls, baseCalls.length));
	}

	@Override
	public int getCoverage() {
		int coverage = 0;
		for (final int c : baseCalls) {
			coverage += c;
		}
		return coverage;
	}

	@Override
	public int getBaseCall(final Base base) {
		return baseCalls[base.getIndex()];
	}

	@Override
	public ArrayBaseCallCount increment(final Base base) {
		baseCalls[base.getIndex()]++;
		return this;
	}

	@Override
	public ArrayBaseCallCount clear() {
		Arrays.fill(baseCalls, 0);
		return this;
	}

	@Override
	public ArrayBaseCallCount add(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			add(base, baseCallCount);
		}
		return this;
	}

	@Override
	public ArrayBaseCallCount set(final Base base, final int count) {
		baseCalls[base.getIndex()] = count;
		return this;
	}

	@Override
	public ArrayBaseCallCount add(final Base base, final BaseCallCount baseCallCount) {
		add(base, base, baseCallCount);
		return this;
	}

	@Override
	public ArrayBaseCallCount add(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		baseCalls[dest.getIndex()] += baseCallCount.getBaseCall(src);
		return this;
	}

	@Override
	public ArrayBaseCallCount subtract(final Base base, final BaseCallCount baseCallCount) {
		subtract(base, base, baseCallCount);
		return this;
	}

	@Override
	public ArrayBaseCallCount subtract(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		this.baseCalls[dest.getIndex()] -= baseCallCount.getBaseCall(src);
		return this;
	}

	@Override
	public ArrayBaseCallCount subtract(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			subtract(base, baseCallCount);
		}
		return this;
	}

	@Override
	public ArrayBaseCallCount invert() {
		for (final Base base : new Base[] { Base.A, Base.C }) {
			final Base complement = base.getComplement();
			if (baseCalls[base.getIndex()] == 0 && baseCalls[complement.getIndex()] == 0) {
				continue;
			}

			final int tmpCount = baseCalls[base.getIndex()];
			baseCalls[base.getIndex()] = baseCalls[complement.getIndex()];
			baseCalls[complement.getIndex()] = tmpCount;
		}
		return this;
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>();

		for (int baseIndex = 0; baseIndex < baseCalls.length; ++baseIndex) {
			if (baseCalls[baseIndex] > 0) {
				final Base base = Base.valueOf(baseIndex);
				alleles.add(base);
			}
		}

		return alleles;
	}

	public String toString() {
		return BaseCallCount.toString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof ArrayBaseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final ArrayBaseCallCount bcc = (ArrayBaseCallCount) obj;
		return baseCalls.equals(bcc.baseCalls);
	}

	@Override
	public int hashCode() {
		return baseCalls.hashCode();
	}
	
	/*
	 * Factory and Parser
	 */

	public static class Factory extends BaseCallCountFactory<ArrayBaseCallCount> {

		@Override
		public ArrayBaseCallCount create() {
			return new ArrayBaseCallCount();
		}

	}

	public static class Parser extends AbstractParser {

		public Parser() {
			super();
		}

		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}

		@Override
		public ArrayBaseCallCount parse(String s) {
			final String[] cols = split(s);
			final int[] baseCalls = new int[cols.length];
			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				baseCalls[baseIndex] = Integer.parseInt(cols[baseIndex]);
			}
			return new ArrayBaseCallCount(baseCalls);
		}

	}

}
