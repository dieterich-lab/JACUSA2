package lib.data.basecall.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
import lib.data.count.BaseCallQualityCount;
import lib.phred2prob.Phred2Prob;

public class ArrayBaseCallQualitityCount implements BaseCallQualityCount {

	private int[][] base2qual2count;
	
	public ArrayBaseCallQualitityCount() {
		base2qual2count = new int[SequenceUtil.VALID_BASES_UPPER.length][Phred2Prob.MAX_Q];
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
	public Set<Byte> getBaseCallQuality(final Base base) {
		final Set<Byte> ret = new HashSet<Byte>(base2qual2count[base.getIndex()].length);
		for (int baseQual = 0; baseQual < Phred2Prob.MAX_Q; baseQual++) {
			if (base2qual2count[base.getIndex()][baseQual] > 0) {
				ret.add((byte)baseQual);
			}
		}
		return ret;
	}
	
	public int getBaseCallQuality(final Base base, byte baseQual) {
		return base2qual2count[base.getIndex()][baseQual];
	}

	@Override
	public void increment(Base base, byte baseQual) {
		base2qual2count[base.getIndex()][baseQual]++;
	}

	@Override
	public void clear() {
		for (final int[] qual2cout : base2qual2count) {
			Arrays.fill(qual2cout, 0);
		}
	}

	@Override
	public void set(final Base base, byte baseQual, int count) {
		base2qual2count[base.getIndex()][baseQual] = count;
	}

	@Override
	public void add(final Base base, BaseCallQualityCount baseCallQualCount) {
		add(base, base, baseCallQualCount);
	}

	@Override
	public void add(final Set<Base> alleles, BaseCallQualityCount baseCallQualCount) {
		for (final Base base : alleles) {
			add(base, baseCallQualCount);
		}
	}

	@Override
	public void add(final Base dest, final Base src, final BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(src)) {
			final int count1 = getBaseCallQuality(dest, baseQual);
			final int count2 = getBaseCallQuality(src, baseQual);
			set(dest, baseQual, count1 + count2);
		}
	}

	@Override
	public void substract(final Base base, final BaseCallQualityCount baseCallQualCount) {
		substract(base, base, baseCallQualCount);
	}

	@Override
	public void substract(final Base dest, final Base src, final BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(src)) {
			final int count1 = getBaseCallQuality(dest, baseQual);
			final int count2 = getBaseCallQuality(src, baseQual);
			set(dest, baseQual, count1 - count2);
		}
	}

	@Override
	public void substract(final Set<Base> alleles, final BaseCallQualityCount baseCallQualCount) {
		for (final Base base : alleles) {
			substract(base, baseCallQualCount);
		}
	}

	@Override
	public void invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (getBaseCallQuality(base).size() == 0 && getBaseCallQuality(complement).size() == 0) {
				continue;
			}
			final int[] tmpCount 					= base2qual2count[base.getIndex()];
			base2qual2count[base.getIndex()]		= base2qual2count[complement.getIndex()];
			base2qual2count[complement.getIndex()]	= tmpCount;
		}
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>(2);
		for (int index = 0; index < base2qual2count.length; index++) {
			final Base base = Base.valueOf(index);
			if (getBaseCallQuality(base).size() > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}
	
}
