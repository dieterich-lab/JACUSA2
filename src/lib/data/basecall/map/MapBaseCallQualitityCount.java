package lib.data.basecall.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
import lib.data.count.BaseCallQualityCount;

public class MapBaseCallQualitityCount implements BaseCallQualityCount {

	private Map<Base, Map<Byte, Integer>> base2qual2count;
	
	public MapBaseCallQualitityCount() {
		base2qual2count = new HashMap<Base, Map<Byte, Integer>>(SequenceUtil.VALID_BASES_UPPER.length);
	}
	
	public MapBaseCallQualitityCount(final MapBaseCallQualitityCount mapBaseCallQualitityCount) {
		this();
		for (final Base base : mapBaseCallQualitityCount.base2qual2count.keySet()) {
			for (final byte baseQual : mapBaseCallQualitityCount.getBaseCallQuality(base)) {
				final int count = mapBaseCallQualitityCount.getBaseCallQuality(base, baseQual);
				set(base, baseQual, count);
			}
		}
	}
	
	@Override
	public BaseCallQualityCount copy() {
		return new MapBaseCallQualitityCount(this);
	}

	@Override
	public Set<Byte> getBaseCallQuality(final Base base) {
		return base2qual2count.containsKey(base) ? new HashSet<Byte>(base2qual2count.get(base).keySet()) : new HashSet<Byte>(0);
	}
	
	@Override
	public int getBaseCallQuality(final Base base, final byte baseQual) {
		if (! base2qual2count.containsKey(base)) {
			return 0;
		}
		
		final Map<Byte, Integer> qual2count = base2qual2count.get(base);
		return qual2count.containsKey(baseQual) ? qual2count.get(baseQual) : 0;
	}

	@Override
	public void increment(final Base base, byte baseQual) {
		final int count = getBaseCallQuality(base, baseQual);
		set(base, baseQual, count + 1);
	}

	@Override
	public void clear() {
		base2qual2count.clear();
	}

	@Override
	public void set(final Base base, final byte baseQual, final int count) {
		if (! base2qual2count.containsKey(base)) {
			base2qual2count.put(base, new HashMap<Byte, Integer>(5));
		}
		final Map<Byte, Integer> qual2count = base2qual2count.get(base);
		qual2count.put(baseQual, count);
	}

	@Override
	public void add(final Base base, final BaseCallQualityCount src) {
		add(base, base, src);
	}

	@Override
	public void add(final Set<Base> alleles, final BaseCallQualityCount mapBaseCallQualitityCount) {
		for (final Base base : alleles) {
			add(base, mapBaseCallQualitityCount);
		}
	}

	@Override
	public void add(final Base dest, final Base src, final BaseCallQualityCount mapBaseCallQualitityCount) {
		for (final byte baseQual : mapBaseCallQualitityCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = mapBaseCallQualitityCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest + countSrc);
		}
	}

	@Override
	public void substract(final Base base, final BaseCallQualityCount mapBaseCallQualitityCount) {
		substract(base, base, mapBaseCallQualitityCount);
	}

	@Override
	public void substract(final Base dest, final Base src, final BaseCallQualityCount mapBaseCallQualitityCount) {
		for (final byte baseQual : mapBaseCallQualitityCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = mapBaseCallQualitityCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest - countSrc);
		}
	}

	@Override
	public void substract(final Base[] alleles, final BaseCallQualityCount mapBaseCallQualitityCount) {
		for (final Base base : alleles) {
			substract(base, mapBaseCallQualitityCount);
		}
	}

	@Override
	public void invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (getBaseCallQuality(base).size() == 0 && getBaseCallQuality(complement).size() == 0) {
				continue;
			}
			final Map<Byte, Integer> tmpCount = base2qual2count.get(base);
			base2qual2count.put(base, base2qual2count.get(complement));
			base2qual2count.put(complement, tmpCount);
			
		}
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>(2);
		for (final Base base : base2qual2count.keySet()) {
			if (getBaseCallQuality(base).size() > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Base base : Base.validValues()) {
			sb.append(base);
			Set<Byte> baseQuals = new TreeSet<Byte>(getBaseCallQuality(base));
			for (final byte baseQual : baseQuals) {
				final int count = getBaseCallQuality(base, baseQual);
				sb.append(' ');
				sb.append(baseQual);
				sb.append('=');
				sb.append(count);
			}
			if (baseQuals.size() == 0) {
				sb.append(' ');
				sb.append("empty");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
}
