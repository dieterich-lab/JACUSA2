package lib.data.basecall.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.count.BaseCallQualityCount;
import lib.phred2prob.Phred2Prob;

public class ArrayBaseCallQualitityCount implements BaseCallQualityCount {

	private int[][] base2qual2count;
	
	public ArrayBaseCallQualitityCount() {
		base2qual2count = new int[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
	}
	
	public ArrayBaseCallQualitityCount(final ArrayBaseCallQualitityCount baseCallQualitityCount) {
		this();
		System.arraycopy(baseCallQualitityCount.base2qual2count, 0, 
				base2qual2count, 0, Phred2Prob.MAX_Q);
	}
	
	@Override
	public BaseCallQualityCount copy() {
		return new ArrayBaseCallQualitityCount(this);
	}

	@Override
	public Set<Byte> getBaseCallQuality(int baseIndex) {
		final Set<Byte> ret = new HashSet<Byte>(base2qual2count[baseIndex].length);
		for (int baseQual = 0; baseQual < Phred2Prob.MAX_Q; baseQual++) {
			if (base2qual2count[baseIndex][baseQual] > 0) {
				ret.add((byte)baseQual);
			}
		}
		return ret;
	}
	
	public int getBaseCallQuality(int baseIndex, byte baseQual) {
		return base2qual2count[baseIndex][baseQual];
	}

	@Override
	public void increment(int baseIndex, byte baseQual) {
		base2qual2count[baseIndex][baseQual]++;
	}

	@Override
	public void clear() {
		for (final int[] qual2cout : base2qual2count) {
			Arrays.fill(qual2cout, 0);
		}
	}

	@Override
	public void set(int baseIndex, byte baseQual, int count) {
		base2qual2count[baseIndex][baseQual] = count;
	}

	@Override
	public void add(int baseIndex, BaseCallQualityCount baseCallQualCount) {
		add(baseIndex, baseIndex, baseCallQualCount);
	}

	@Override
	public void add(final Set<Integer> alleles, BaseCallQualityCount baseCallQualCount) {
		for (final int baseIndex : alleles) {
			add(baseIndex, baseCallQualCount);
		}
	}

	@Override
	public void add(int baseIndexDest, int baseIndexSrc, BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(baseIndexSrc)) {
			final int count1 = getBaseCallQuality(baseIndexDest, baseQual);
			final int count2 = getBaseCallQuality(baseIndexSrc, baseQual);
			set(baseIndexDest, baseQual, count1 + count2);
		}
	}

	@Override
	public void substract(int baseIndex, BaseCallQualityCount baseCallQualCount) {
		substract(baseIndex, baseIndex, baseCallQualCount);
	}

	@Override
	public void substract(int baseIndexDest, int baseIndexSrc, BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(baseIndexSrc)) {
			final int count1 = getBaseCallQuality(baseIndexDest, baseQual);
			final int count2 = getBaseCallQuality(baseIndexSrc, baseQual);
			set(baseIndexDest, baseQual, count1 - count2);
		}
	}

	@Override
	public void substract(final int[] alleles, final BaseCallQualityCount baseCallQualCount) {
		for (final int baseIndex : alleles) {
			substract(baseIndex, baseCallQualCount);
		}
	}

	@Override
	public void invert() {
		for (final int baseIndex : new int[] {0, 1}) {
			final int complementaryBaseIndex 		= BaseCallConfig.BASES.length - baseIndex - 1;
			if (getBaseCallQuality(baseIndex).size() == 0 && getBaseCallQuality(complementaryBaseIndex).size() == 0) {
				continue;
			}
			final int[] tmpCount 					= base2qual2count[baseIndex];
			base2qual2count[baseIndex]				= base2qual2count[complementaryBaseIndex];
			base2qual2count[complementaryBaseIndex] = tmpCount;
		}
	}

	@Override
	public Set<Integer> getAlleles() {
		final Set<Integer> alleles = new HashSet<Integer>(2);
		for (int baseIndex = 0; baseIndex < base2qual2count.length; baseIndex++) {
			if (getBaseCallQuality(baseIndex).size() > 0) {
				alleles.add(baseIndex);
			}
		}
		return alleles;
	}
	
}
