package lib.data.basecall.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
import lib.data.count.BaseCallCount;

public class ArrayBaseCallCount 
implements BaseCallCount {

	// container
	private int[] baseCall;

	public ArrayBaseCallCount() {
		baseCall = new int[SequenceUtil.VALID_BASES_UPPER.length];
	}

	public ArrayBaseCallCount(final int[] baseCall) {
		this();
		System.arraycopy(baseCall, 0, this.baseCall, 0, baseCall.length);
	}

	public ArrayBaseCallCount(final BaseCallCount baseCallCount) {
		this();
		add(baseCallCount);
	}
	
	public ArrayBaseCallCount(final ArrayBaseCallCount baseCallCount) {
		this(baseCallCount.baseCall);
	}

	public ArrayBaseCallCount copy() {
		return new ArrayBaseCallCount(this);
	}

	public int getCoverage() {
		int coverage = 0;
		
		for (final int c : baseCall) {
			coverage += c;
		}

		return coverage;
	}
	
	@Override
	public int getBaseCall(final Base base) {
		return baseCall[base.getIndex()];
	}

	@Override
	public void increment(final Base base) {
		baseCall[base.getIndex()]++;
	}

	@Override
	public void clear() {
		Arrays.fill(baseCall, 0);
	}

	@Override
	public void add(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			add(base, baseCallCount);
		}
	}

	@Override
	public void set(final Base base, final int count) {
		baseCall[base.getIndex()] = count;
	}

	@Override
	public void add(final Base base, final BaseCallCount baseCallCount) {
		add(base, base, baseCallCount);
	}

	@Override
	public void add(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		baseCall[dest.getIndex()] += baseCallCount.getBaseCall(src);
	}
	
	@Override
	public void substract(final Base base, final BaseCallCount baseCallCount) {
		substract(base, base, baseCallCount);
	}

	@Override
	public void substract(final Base dest, final Base src, final BaseCallCount baseCallCount) {
		this.baseCall[dest.getIndex()] -= baseCallCount.getBaseCall(src);
	}
	
	@Override
	public void substract(final BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
				substract(base, baseCallCount);
		}
	}
	
	@Override
	public void invert() {
		for (final Base base : new Base[]{Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (baseCall[base.getIndex()] == 0 && baseCall[complement.getIndex()] == 0) {
				continue;
			}

			final int tmpCount					= baseCall[base.getIndex()];
			baseCall[base.getIndex()] 			= baseCall[complement.getIndex()];
			baseCall[complement.getIndex()] 	= tmpCount;
		}
	}
	
	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>(2);
	
		for (int baseIndex = 0; baseIndex < baseCall.length; ++baseIndex) {
			if (baseCall[baseIndex] > 0) {
				alleles.add(Base.valueOf(baseIndex));
			}
		}

		return alleles;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		int i = 0;
		final int n = baseCall.length;
		sb.append("(");
		sb.append(Base.valueOf(i));
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(Base.valueOf(i));
		}
		sb.append(") (");
		
		i = 0;
		sb.append(baseCall[i]);
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(baseCall[i]);
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
