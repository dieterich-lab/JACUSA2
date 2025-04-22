package lib.data.count.basecallquality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.util.StringUtil;
import lib.util.Base;

public interface BaseCallQualityCount extends Serializable {

	BaseCallQualityCount copy();

	Set<Base> getAlleles();
	Set<Byte> getBaseCallQuality(Base base);
	int getBaseCallQuality(Base base, byte baseQual);
	
	default int getCoverage() {
		int coverage = 0;
		for (final Base base : getAlleles()) {
			for (final Byte baseQual : getBaseCallQuality(base)) {
				coverage += getBaseCallQuality(base, baseQual);
			}
		}
		return coverage;
	}
	
	BaseCallQualityCount increment(Base base, byte baseQual);

	BaseCallQualityCount clear();

	BaseCallQualityCount set(Base base, byte baseQual, int count);
	BaseCallQualityCount add(Base base, BaseCallQualityCount baseCallQualCount);
	BaseCallQualityCount add(Set<Base> alleles, BaseCallQualityCount baseCallQualCount);
	BaseCallQualityCount add(Base dest, Base src, BaseCallQualityCount baseCallQualCount);
	
	BaseCallQualityCount subtract(Base base, BaseCallQualityCount baseCallQualCount);
	BaseCallQualityCount subtract(Set<Base> alleles, BaseCallQualityCount baseCallQualCount);
	BaseCallQualityCount subtract(Base dest, Base src, BaseCallQualityCount baseCallQualCount);

	BaseCallQualityCount invert();
	
	String toString();
	
	default String toString(final BaseCallQualityCount bcqc) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Base call qual count:\n");
		for (final Base base : Base.validValues()) {
			final List<String> baseQualStr = new ArrayList<>();
			for (final byte baseQual : bcqc.getBaseCallQuality(base)) {
				final int count = bcqc.getBaseCallQuality(base, baseQual);
				if (count > 0) {
					baseQualStr.add(count + "x" + Byte.toString(baseQual));
				}
			}
			sb.append(' ');
			if (baseQualStr.isEmpty()) {
				sb.append('*');
			} else {
				sb.append(StringUtil.join(",", baseQualStr));
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	default boolean specificEquals(final BaseCallQualityCount bcqc) {
		if (bcqc == null) {
			return false;
		}
		if (bcqc == this) {
			return true;
		}
		final Set<Base> bases = getAlleles();
		bases.addAll(bcqc.getAlleles());
		for (final Base base : bases) {
			final Set<Byte> baseQuals = getBaseCallQuality(base);
			baseQuals.addAll(bcqc.getBaseCallQuality(base));
			for (final byte baseQual : baseQuals) {
				if (getBaseCallQuality(base, baseQual) != bcqc.getBaseCallQuality(base, baseQual)) {
					return false;
				}
			}
		}
		return true;
	}

	// globally create BaseCallQualtityCount objects - there are different implementations
	// used to define PileupCount for variant calling
	public static BaseCallQualityCount create() {
		return new MapBaseCallQualityCount();
	}
	
	/*
	 * Parser
	 */
	
	public abstract static class AbstractParser implements lib.util.Parser<BaseCallQualityCount> {
		
		public static final char BASE_CALL_SEP 	= ',';
		public static final char QUAL_SEP 		= ';';
		public static final char EMPTY 			= '*';

		private final char baseCallSep;
		private final char qualSep;
		private final char empty;
	
		public AbstractParser() {
			this(BASE_CALL_SEP, QUAL_SEP, EMPTY);
		}
		
		public AbstractParser(final char baseCallSep, final char qualSep, final char empty) {
			this.baseCallSep 	= baseCallSep;
			this.qualSep 		= qualSep;
			this.empty 			= empty;
		}

		protected void parse(String s, final BaseCallQualityCount bcqc) {
			if (s.equals(Character.toString(empty))) {
				s = StringUtil.join(
						Character.toString(baseCallSep), 
						Collections.nCopies(
								Base.validValues().length, 
								Character.toString(empty)) );
			}
			final String[] baseQualStr = s.split(Character.toString(baseCallSep));
			final int validValues = Base.validValues().length;
			if (baseQualStr.length != validValues) {
				throw new IllegalArgumentException("Size of parsed s != validValues: " + s);
			}
			for (int baseIndex = 0; baseIndex < validValues; ++baseIndex) {
				final Base base = Base.valueOf(baseIndex);
				final String qualStr = baseQualStr[baseIndex];
				if (! qualStr.equals(Character.toString(empty))) {
					for (final String qual : qualStr.split(Character.toString(qualSep))) {
						bcqc.increment(base, Byte.parseByte(qual));
					}
				}
			}
		}
		
		@Override
		public String wrap(BaseCallQualityCount o) {
			final StringBuilder sb = new StringBuilder();
			int count = 0;
			boolean first = true;
			for (final Base base : Base.validValues()) {
				if (! first) {
					sb.append(baseCallSep);
				} else {
					first = false;
				}
				final Set<Byte> baseQuals = o.getBaseCallQuality(base);
				final List<String> qualList = new ArrayList<>();
				for (final byte baseQual : baseQuals) {
					final int tmpCount = o.getBaseCallQuality(base, baseQual);
					if (tmpCount == 0) {
						continue;
					}
					final List<String> tmpQualList = Collections.nCopies(
							tmpCount, 
							Byte.toString(baseQual));
					qualList.addAll(tmpQualList);
					count += tmpCount;
				}
				if (qualList.isEmpty()) {
					sb.append(Character.toString(empty));
				} else {
					sb.append(StringUtil.join(
							Character.toString(qualSep), 
							qualList) );
				}
			}
			if (count == 0) {
				return Character.toString(empty);
			}
			return sb.toString();
		}
		
		public char getEmpty() {
			return empty;
		}
		
	}

}
