package lib.data.cache.lrtarrest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.StringUtil;
import jacusa.JACUSA;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBaseCallCount;
import lib.util.Base;
import lib.util.Data;
import lib.util.coordinate.Coordinate;

public class ArrestPos2BaseCallCount 
implements Serializable, Data<ArrestPos2BaseCallCount> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * stores base call count information for each arrest position
	 */
	private Map<Integer, BaseCallCount> arrest2bcc;

	public ArrestPos2BaseCallCount() {
		arrest2bcc = new HashMap<Integer, BaseCallCount>(30);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public ArrestPos2BaseCallCount(final ArrestPos2BaseCallCount src) {
		this();
		merge(src);
	}

	/**
	 * Adds Base at the provided arrestPos.
	 * 
	 * @param arrestPos position of the arrest where to add base
	 * @param base the base to be added
	 */
	public ArrestPos2BaseCallCount addBaseCall(final int arrestPos, final Base base, final Coordinate window) {
		if (! arrest2bcc.containsKey(arrestPos)) {
			arrest2bcc.put(arrestPos, JACUSA.bccFactory.create());
		}
		final BaseCallCount baseCallCount = arrest2bcc.get(arrestPos);
		baseCallCount.increment(base);
		return this;
	}
	
	public boolean contains(final int arrestPos) {
		return arrest2bcc.containsKey(arrestPos);
	}

	/**
	 * Returns sorted arrest positions
	 * 
	 * @return sorted arrest positions
	 */
	public Set<Integer> getArrestPos() {
		return new TreeSet<Integer>(arrest2bcc.keySet());
	}

	/**
	 * Returns base call count for arrest position.
	 * 
	 * @param arrestPos arrest position of interest
	 * @return base call count at requested arrest position;
	 */
	public BaseCallCount getArrestBaseCallCount(final int arrestPos) {
		if (! contains(arrestPos)) {
			return new UnmodifiableBaseCallCount(JACUSA.bccFactory.create());
		}
		return new UnmodifiableBaseCallCount(arrest2bcc.get(arrestPos));
	}
	
	/**
	 * Returns base call count where arrest position != pos (total - arrest = through)
	 * 
	 * @param refPos arrest position to be excluded
	 * @return base call count excluding arrest position pos
	 */
	public BaseCallCount getThroughBaseCallCount(final int refPos) {
		final BaseCallCount total = getTotalBaseCallCountHelper();
		if (contains(refPos)) {
			total.subtract(getArrestBaseCallCount(refPos));
		}
		return new UnmodifiableBaseCallCount(total);
	}
	
	private BaseCallCount getTotalBaseCallCountHelper() {
		final BaseCallCount bcc = JACUSA.bccFactory.create();
		for (final BaseCallCount tmpBcc : arrest2bcc.values()) {
			bcc.add(tmpBcc);
		}
		return bcc;
	}
	
	/**
	 * Returns total base call count, including base call counts from all arrest positions
	 * 
	 * @return base call count, summed over all arrest positions
	 */
	public BaseCallCount getTotalBaseCallCount() {
		return new UnmodifiableBaseCallCount(getTotalBaseCallCountHelper());
	}

	/**
	 * Adds arrest positions and corresponding base call counts 
	 * 
	 * @param src ArrestPos2BaseCallCount to be added
	 */
	@Override
	public void merge(final ArrestPos2BaseCallCount src) {
		for (final int arrestPos : src.getArrestPos()) {
			if (! contains(arrestPos)) {
				arrest2bcc.put(arrestPos, src.arrest2bcc.get(arrestPos).copy());
			} else {
				arrest2bcc.get(arrestPos)
					.add(src.arrest2bcc.get(arrestPos));
			}
		}
	}
	
	/**
	 * Creates a deep copy
	 * 
	 * @return a deep copy
	 */
	@Override
	public ArrestPos2BaseCallCount copy() {
		return new ArrestPos2BaseCallCount(this);
	}
	
	/**
	 * Clears internal data structures
	 */
	public void clear() {
		arrest2bcc.clear();
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof ArrestPos2BaseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final ArrestPos2BaseCallCount ap2bcc = (ArrestPos2BaseCallCount)obj;
		return arrest2bcc.equals(ap2bcc.arrest2bcc);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + arrest2bcc.hashCode();
		return hash;
	}
	
	/**
	 * Pretty print of content
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ArrestPos\tArrestBaseCallCount\tThroughBaseCallCount\n");
		for (final int arrestPos : getArrestPos()) {
			sb.append(arrestPos);
			sb.append("\t");
			sb.append(BaseCallCount.toString(getArrestBaseCallCount(arrestPos)));
			sb.append('\t');
			sb.append(BaseCallCount.toString(getThroughBaseCallCount(arrestPos)));
			sb.append('\n');
		}
		sb.append("Total base call count: ");
		sb.append(BaseCallCount.toString(getTotalBaseCallCount()));
		return sb.toString();
	}
	
	public static class Parser implements lib.util.Parser<ArrestPos2BaseCallCount> {
		
		public static final char ARREST_POS_SEP = ',';
		public static final char FIELD_SEP = ':';
		public static final char EMPTY = '*';

		private final char arrestPosSep;
		private final char fieldSep;
		private final char empty;

		private final Pattern pattern;
		private final BaseCallCount.AbstractParser bccParser;
		
		public Parser() {
			this(ARREST_POS_SEP,
					FIELD_SEP,
					EMPTY,
					new ArrayBaseCallCount.Parser() );
		}

		public Parser(final char arrestPosSep, final char fieldSep, final char empty, final BaseCallCount.AbstractParser bccParser) {
			this.arrestPosSep = arrestPosSep;
			this.fieldSep = fieldSep;
			this.empty = empty;

			pattern = Pattern.compile(
					"([0-9]+)" + 
					this.fieldSep + 
					"([^" + this.fieldSep +  this.arrestPosSep + "]+)");
			this.bccParser = bccParser;
		}
		
		@Override
		public ArrestPos2BaseCallCount parse(String s) {
			final int i = s.indexOf(Character.toString(ARREST_POS_SEP)); 
			if (i < 0) {
				throw new IllegalStateException("Current Position could not be parser");
			}
		
			final ArrestPos2BaseCallCount o = new ArrestPos2BaseCallCount();
			if (s.equals(Character.toString(empty))) {
				return o;
			}

			final int arrestPositions = s.split(Character.toString(arrestPosSep)).length;

			final Matcher match = pattern.matcher(s);
			while (match.find()) {
				final int arrestPos = Integer.parseInt(match.group(1));
				if (arrestPos <= 0) {
					throw new IllegalArgumentException("Arrest position cannot be negative: " + arrestPos);
				}
				final BaseCallCount bcc = bccParser.parse(match.group(2));
				if (o.arrest2bcc.containsKey(arrestPos)) {
	        		throw new IllegalStateException("Duplicate arrest position: " + arrestPos);
	        	}
	        	o.arrest2bcc.put(arrestPos, bcc);
	        }
			if (arrestPositions != o.getArrestPos().size()) {
	        	throw new IllegalArgumentException("Size of parsed sites does not match: " + s);
			}
			if (o.getArrestPos().size() == 0) {
				throw new IllegalArgumentException("Cannot parse arrest positions from: " + s);
			}
			return o;
		}
		
		@Override
		public String wrap(final ArrestPos2BaseCallCount o) {
			if (o.getArrestPos().size() == 0) {
				return Character.toString(empty);
			}

			final List<String> strList = new ArrayList<>();
			for (final int arrestPos : o.getArrestPos()) {
				final BaseCallCount bcc = o.getArrestBaseCallCount(arrestPos);
				strList.add(
						StringUtil.join(
								Character.toString(FIELD_SEP), 
								Arrays.asList(
										Integer.toString(arrestPos),
										bccParser.wrap(bcc))) );
			}

			return StringUtil.join(
					Character.toString(ARREST_POS_SEP), strList);
		}
	}
	
}
