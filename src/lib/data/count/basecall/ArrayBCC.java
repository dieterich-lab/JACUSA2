package lib.data.count.basecall;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import lib.util.Base;

public class ArrayBCC extends AbstractBCC {

	private static final long serialVersionUID = 1L;

	// container
	private final int[] baseCalls;

	public ArrayBCC() {
		this.baseCalls = new int[Base.validValues().length];
	}

	public ArrayBCC(final int[] baseCalls) {
		if (baseCalls.length != Base.validValues().length) {
			throw new IllegalArgumentException("baseCalls != Base.validValues().length");
		}
		this.baseCalls = baseCalls;
	}

	@Override
	public ArrayBCC copy() {
		return new ArrayBCC(Arrays.copyOf(baseCalls, baseCalls.length));
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
	public ArrayBCC increment(final Base base) {
		baseCalls[base.getIndex()]++;
		return this;
	}

	@Override
	public ArrayBCC clear() {
		Arrays.fill(baseCalls, 0);
		return this;
	}

	@Override
	public ArrayBCC add(final BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			add(base, bcc);
		}
		return this;
	}

	@Override
	public ArrayBCC set(final Base base, final int count) {
		baseCalls[base.getIndex()] = count;
		return this;
	}

	@Override
	public ArrayBCC add(final Base base, final BaseCallCount bcc) {
		add(base, base, bcc);
		return this;
	}

	@Override
	public ArrayBCC add(final Base dest, final Base src, final BaseCallCount bcc) {
		baseCalls[dest.getIndex()] += bcc.getBaseCall(src);
		return this;
	}

	@Override
	public ArrayBCC subtract(final Base base, final BaseCallCount bcc) {
		subtract(base, base, bcc);
		return this;
	}

	@Override
	public ArrayBCC subtract(final Base dest, final Base src, final BaseCallCount bcc) {
		this.baseCalls[dest.getIndex()] -= bcc.getBaseCall(src);
		return this;
	}

	@Override
	public ArrayBCC subtract(final BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			subtract(base, bcc);
		}
		return this;
	}

	@Override
	public ArrayBCC invert() {
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
		final Set<Base> alleles = new TreeSet<Base>();

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
	
	/*
	 * Parser
	 */

	public static class Parser extends AbstractParser {

		public Parser() {
			super();
		}

		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}

		@Override
		public ArrayBCC parse(String s) {
			final String[] cols = split(s);
			final int[] baseCalls = new int[cols.length];
			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				baseCalls[baseIndex] = Integer.parseInt(cols[baseIndex]);
				if (baseCalls[baseIndex] < 0) {
					throw new IllegalArgumentException(); 
				}
			}
			return new ArrayBCC(baseCalls);
		}

	}

}
