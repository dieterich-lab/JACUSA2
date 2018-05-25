package lib.data.basecall.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lib.cli.options.BaseCallConfig;
import lib.data.count.BaseCallQualityCount;

public class MapBaseCallQualitityCount implements BaseCallQualityCount {

	private Map<Integer, Map<Byte, Integer>> base2qual2count;
	
	public MapBaseCallQualitityCount() {
		base2qual2count = new HashMap<Integer, Map<Byte,Integer>>(BaseCallConfig.BASES.length);
	}
	
	public MapBaseCallQualitityCount(final MapBaseCallQualitityCount src) {
		this();
		for (final int baseIndex : src.base2qual2count.keySet()) {
			for (final byte baseQual : src.getBaseCallQuality(baseIndex)) {
				final int count = src.getBaseCallQuality(baseIndex, baseQual);
				set(baseIndex, baseQual, count);
			}
		}
	}
	
	@Override
	public BaseCallQualityCount copy() {
		return new MapBaseCallQualitityCount(this);
	}

	@Override
	public Set<Byte> getBaseCallQuality(final int baseIndex) {
		return base2qual2count.containsKey(baseIndex) ? new HashSet<Byte>(base2qual2count.get(baseIndex).keySet()) : new HashSet<Byte>(0);
	}
	
	@Override
	public int getBaseCallQuality(final int baseIndex, final byte baseQual) {
		if (! base2qual2count.containsKey(baseIndex)) {
			return 0;
		}
		
		final Map<Byte, Integer> qual2count = base2qual2count.get(baseIndex);
		return qual2count.containsKey(baseQual) ? qual2count.get(baseQual) : 0;
	}

	@Override
	public void increment(int baseIndex, byte baseQual) {
		final int count = getBaseCallQuality(baseIndex, baseQual);
		set(baseIndex, baseQual, count + 1);
	}

	@Override
	public void clear() {
		base2qual2count.clear();
	}

	@Override
	public void set(final int baseIndex, final byte baseQual, final int count) {
		if (! base2qual2count.containsKey(baseIndex)) {
			base2qual2count.put(baseIndex, new HashMap<Byte, Integer>(5));
		}
		final Map<Byte, Integer> qual2count = base2qual2count.get(baseIndex);
		qual2count.put(baseQual, count);
	}

	@Override
	public void add(final int baseIndex, final BaseCallQualityCount src) {
		add(baseIndex, baseIndex, src);
	}

	@Override
	public void add(final Set<Integer> alleles, final BaseCallQualityCount src) {
		for (final int baseIndex : alleles) {
			add(baseIndex, src);
		}
	}

	@Override
	public void add(final int baseIndexDest, final int baseIndexSrc, final BaseCallQualityCount src) {
		for (final byte baseQual : src.getBaseCallQuality(baseIndexSrc)) {
			final int countDest = getBaseCallQuality(baseIndexDest, baseQual);
			final int countSrc = src.getBaseCallQuality(baseIndexSrc, baseQual);
			set(baseIndexDest, baseQual, countDest + countSrc);
		}
	}

	@Override
	public void substract(final int baseIndex, final BaseCallQualityCount src) {
		substract(baseIndex, baseIndex, src);
	}

	@Override
	public void substract(final int baseIndexDest, final int baseIndexSrc, final BaseCallQualityCount src) {
		for (final byte baseQual : src.getBaseCallQuality(baseIndexSrc)) {
			final int countDest = getBaseCallQuality(baseIndexDest, baseQual);
			final int countSrc = src.getBaseCallQuality(baseIndexSrc, baseQual);
			set(baseIndexDest, baseQual, countDest - countSrc);
		}
	}

	@Override
	public void substract(final int[] alleles, final BaseCallQualityCount src) {
		for (final int baseIndex : alleles) {
			substract(baseIndex, src);
		}
	}

	@Override
	public void invert() {
		for (final int baseIndex : new int[] {0, 1}) {
			final int complementaryBaseIndex = BaseCallConfig.BASES.length - baseIndex - 1;
			if (getBaseCallQuality(baseIndex).size() == 0 && getBaseCallQuality(complementaryBaseIndex).size() == 0) {
				continue;
			}
			final Map<Byte, Integer> tmpCount = base2qual2count.get(baseIndex);
			base2qual2count.put(baseIndex, base2qual2count.get(complementaryBaseIndex));
			base2qual2count.put(complementaryBaseIndex, tmpCount);
			
		}
	}

	@Override
	public Set<Integer> getAlleles() {
		final Set<Integer> alleles = new HashSet<Integer>(2);
		for (final int baseIndex : base2qual2count.keySet()) {
			if (getBaseCallQuality(baseIndex).size() > 0) {
				alleles.add(baseIndex);
			}
		}
		return alleles;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final char base : BaseCallConfig.BASES) {
			sb.append(base);
			final int baseIndex = BaseCallConfig.getInstance().getBaseIndex((byte)base);
			Set<Byte> baseQuals = new TreeSet<Byte>(getBaseCallQuality(baseIndex));
			for (final byte baseQual : baseQuals) {
				final int count = getBaseCallQuality(baseIndex, baseQual);
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
