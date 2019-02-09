package lib.data.count.basecallquality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lib.util.Base;
import lib.util.Util;

public class MapBaseCallQualityCount implements BaseCallQualityCount {

	private final Map<Base, Map<Byte, Integer>> baseCallQuals;
	
	public MapBaseCallQualityCount() {
		baseCallQuals = new HashMap<>(4);
	}
	
	public MapBaseCallQualityCount(final Map<Base, Map<Byte, Integer>> baseCallQuals) {
		for (final Base base : baseCallQuals.keySet()) {
			for (final byte qual : baseCallQuals.get(base).keySet()) {
				if (qual < 0) {
					throw new IllegalStateException("qual must be >= 0: " + qual);	
				}
			}				
		}
		this.baseCallQuals = baseCallQuals;
	}
	
	@Override
	public BaseCallQualityCount copy() {
		final Map<Base, Map<Byte, Integer>> tmp = new HashMap<>(Util.noRehashCapacity(baseCallQuals.size()));
		for (final Base base : baseCallQuals.keySet()) {
			if (baseCallQuals.containsKey(base) && baseCallQuals.get(base) != null) {
				tmp.put(base, new HashMap<>(baseCallQuals.get(base)));
			}
		}
		return new MapBaseCallQualityCount(tmp);
	}

	@Override
	public Set<Byte> getBaseCallQuality(final Base base) {
		return baseCallQuals.containsKey(base) && baseCallQuals.get(base) != null ? new TreeSet<Byte>(baseCallQuals.get(base).keySet()) : new HashSet<Byte>(0);
	}
	
	@Override
	public int getBaseCallQuality(final Base base, final byte baseQual) {
		if (! baseCallQuals.containsKey(base)) {
			return 0;
		}
		
		final Map<Byte, Integer> qual2count = baseCallQuals.get(base);
		return qual2count.containsKey(baseQual) ? qual2count.get(baseQual) : 0;
	}

	@Override
	public MapBaseCallQualityCount increment(final Base base, byte baseQual) {
		final int count = getBaseCallQuality(base, baseQual);
		set(base, baseQual, count + 1);
		return this;
	}

	@Override
	public MapBaseCallQualityCount clear() {
		baseCallQuals.clear();
		return this;
	}

	@Override
	public MapBaseCallQualityCount set(final Base base, final byte baseQual, final int count) {
		if (! baseCallQuals.containsKey(base)) {
			baseCallQuals.put(base, new HashMap<Byte, Integer>(8));
		}
		final Map<Byte, Integer> qual2count = baseCallQuals.get(base);
		qual2count.put(baseQual, count);
		return this;
	}

	@Override
	public MapBaseCallQualityCount add(final Base base, final BaseCallQualityCount baseCallQualitityCount) {
		add(base, base, baseCallQualitityCount);
		return this;
	}

	@Override
	public MapBaseCallQualityCount add(final Set<Base> alleles, final BaseCallQualityCount baseCallQualitityCount) {
		for (final Base base : alleles) {
			add(base, baseCallQualitityCount);
		}
		return this;
	}

	@Override
	public MapBaseCallQualityCount add(final Base dest, final Base src, final BaseCallQualityCount baseCallQualitityCount) {
		for (final byte baseQual : baseCallQualitityCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = baseCallQualitityCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest + countSrc);
		}
		return this;
	}

	@Override
	public MapBaseCallQualityCount subtract(final Base base, final BaseCallQualityCount baseCallQualitityCount) {
		subtract(base, base, baseCallQualitityCount);
		return this;
	}

	@Override
	public MapBaseCallQualityCount subtract(final Base dest, final Base src, final BaseCallQualityCount baseCallQualitityCount) {
		for (final byte baseQual : baseCallQualitityCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = baseCallQualitityCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest - countSrc);
		}
		return this;
	}

	@Override
	public MapBaseCallQualityCount subtract(final Set<Base> alleles, final BaseCallQualityCount mapBaseCallQualitityCount) {
		for (final Base base : alleles) {
			subtract(base, mapBaseCallQualitityCount);
		}
		return this;
	}

	@Override
	public MapBaseCallQualityCount invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (getBaseCallQuality(base).size() == 0 && getBaseCallQuality(complement).size() == 0) {
				continue;
			}
			final Map<Byte, Integer> tmpCount = baseCallQuals.get(base);
			baseCallQuals.put(base, baseCallQuals.get(complement));
			baseCallQuals.put(complement, tmpCount);
		}
		return this;
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>(2);
		for (final Base base : baseCallQuals.keySet()) {
			if (getBaseCallQuality(base).size() > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}

	@Override
	public String toString() {
		return toString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof MapBaseCallQualityCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final MapBaseCallQualityCount bcqc = (MapBaseCallQualityCount)obj;
		return baseCallQuals.equals(bcqc.baseCallQuals);
	}
	
	@Override
	public int hashCode() {
		return baseCallQuals.hashCode();
	}
	
	/*
	 * Factory and Parser
	 */
	
	public static class Factory extends BaseCallQualityCountFactory<MapBaseCallQualityCount> {
		
		@Override
		public MapBaseCallQualityCount create() {
			return new MapBaseCallQualityCount();
		}

	}
	
	public static class Parser extends BaseCallQualityCount.AbstractParser {

		public Parser() {
			super();
		}
		
		public Parser(final char baseCallSep, final char qualSep, final char empty) {
			super(baseCallSep, qualSep, empty);
		}
		
		@Override
		public MapBaseCallQualityCount parse(String s) {
			MapBaseCallQualityCount bcqc = new MapBaseCallQualityCount();
			parse(s, bcqc);
			return bcqc;
		}
		
	}
	
}
