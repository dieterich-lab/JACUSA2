package lib.data.basecall.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.count.BaseCallCount;

public class ArrayBaseCallCount 
implements BaseCallCount {

	// container
	private int[] baseCall;

	public ArrayBaseCallCount() {
		baseCall = new int[BaseCallConfig.BASES.length];
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
	
	public int[] getArray() {
		return baseCall;
	}
	
	@Override
	public int getBaseCall(final int baseIndex) {
		return baseCall[baseIndex];
	}

	@Override
	public void increment(final int baseIndex) {
		baseCall[baseIndex]++;
	}

	@Override
	public void clear() {
		Arrays.fill(baseCall, 0);
	}

	@Override
	public void add(final BaseCallCount src) {
		for (final int baseIndex : src.getAlleles()) {
			add(baseIndex, src);
		}
	}

	@Override
	public void set(final int baseIndex, final int count) {
		baseCall[baseIndex] = count;
	}

	@Override
	public void add(final int baseIndex, final BaseCallCount src) {
		add(baseIndex, baseIndex, src);
	}

	@Override
	public void add(final int baseIndexDest, final int baseIndexSrc, final BaseCallCount src) {
		baseCall[baseIndexDest] += src.getBaseCall(baseIndexSrc);
	}
	
	@Override
	public void substract(final int baseIndex, final BaseCallCount src) {
		substract(baseIndex, baseIndex, src);
	}

	@Override
	public void substract(final int baseIndexDest, final int baseIndexSrc, final BaseCallCount src) {
		this.baseCall[baseIndexDest] -= src.getBaseCall(baseIndexSrc);
	}
	
	@Override
	public void substract(final BaseCallCount src) {
		for (final int baseIndex : src.getAlleles()) {
				substract(baseIndex, src);
		}
	}
	
	@Override
	public void invert() {
		final int[] tmp = new int[baseCall.length];
		for (final int baseIndex : getAlleles()) {
			final int complementaryBaseIndex 	= baseCall.length - baseIndex - 1;
			tmp[complementaryBaseIndex]			= baseCall[baseIndex];
			tmp[baseIndex] 						= baseCall[complementaryBaseIndex];
		}
		baseCall = tmp;
	}

	@Override
	public Set<Integer> getAlleles() {
		final Set<Integer> alleles = new HashSet<Integer>(2);
	
		for (int baseIndex = 0; baseIndex < baseCall.length; ++baseIndex) {
			if (baseCall[baseIndex] > 0) {
				alleles.add(baseIndex);
			}
		}

		return alleles;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		int i = 0;
		final int n = baseCall.length;
		sb.append("(");
		sb.append(BaseCallConfig.BASES[i]);
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(BaseCallConfig.BASES[i]);
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
