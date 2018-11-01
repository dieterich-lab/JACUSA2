package lib.data.count.basecall;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import htsjdk.samtools.util.StringUtil;
import jacusa.JACUSA;
import lib.data.has.HasCoverage;
import lib.util.Base;
import lib.util.Data;
import lib.util.Util;

public interface BaseCallCount extends Data<BaseCallCount>, HasCoverage, Serializable {
	
	int getBaseCall(Base base);
	Set<Base> getAlleles();
	
	BaseCallCount increment(Base base);

	BaseCallCount clear();

	BaseCallCount set(Base base, int count);

	BaseCallCount add(Base base, BaseCallCount baseCallCount);
	BaseCallCount add(BaseCallCount baseCallCount);
	BaseCallCount add(Base dest, Base src, BaseCallCount baseCallCount);
	
	@Override
	default void merge(BaseCallCount o) {
		add(o);
	}
	
	static BaseCallCount newInstance() {
		return JACUSA.bccFactory.create();
	}
	
	BaseCallCount subtract(Base base, BaseCallCount baseCallCount);
	BaseCallCount subtract(BaseCallCount baseCallCount);
	BaseCallCount subtract(Base dest, Base src, BaseCallCount baseCallCount);

	BaseCallCount invert();
	
	String toString();

	public static String toString(BaseCallCount baseCallCount) {
		return new ArrayBaseCallCount.Parser(Util.FIELD_SEP, Util.EMPTY_FIELD)
				.wrap(baseCallCount);
	}
	
	/*
	 * Parser
	 */
	
	public static abstract class AbstractParser implements lib.util.Parser<BaseCallCount> {

		public static final char BASE_CALL_SEP = ';';
		public static final char EMPTY = '*';
		
		private final char baseCallSep;
		private final char empty;
		
		public AbstractParser() {
			this(BASE_CALL_SEP, EMPTY);
		}
		
		public AbstractParser(final char baseCallSep, final char empty) {
			this.baseCallSep = baseCallSep;
			this.empty = empty;
		}

		protected String[] split(String s) {
			if (s.equals(Character.toString(empty))) {
				s = StringUtil.join(
						Character.toString(baseCallSep), 
						Collections.nCopies(Base.validValues().length, 0) );
			}

			final String[] cols = s.split(Character.toString(baseCallSep));
			final int validValues = Base.validValues().length;
			if (cols.length != validValues) {
				throw new IllegalArgumentException("Size of parsed s != validValues: " + s);
			}
			return cols;
		}
		
		@Override
		public String wrap(BaseCallCount o) {
			if (o.getCoverage() == 0) {
				return Character.toString(empty);
			}

			final StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (final Base base : Base.validValues()) {
				if (! first) {
					sb.append(baseCallSep);
				} else {
					first = false;
				}
				sb.append(o.getBaseCall(base));
			}
			return sb.toString();
		}

		public char getEmpty() {
			return empty;
		}
		
		public char getBaseCallSep() {
			return baseCallSep;
		}
		
	}

}
