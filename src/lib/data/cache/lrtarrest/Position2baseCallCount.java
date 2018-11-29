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

public class Position2baseCallCount 
implements Serializable, Data<Position2baseCallCount> {

	private static final long serialVersionUID = 1L;
	
	private static final BaseCallCount EMPTY = JACUSA.bccFactory.create();
	
	/**
	 * stores base call count information for each position
	 */
	private Map<Integer, BaseCallCount> pos2bcc;
	
	public Position2baseCallCount() {
		pos2bcc = new HashMap<Integer, BaseCallCount>(3);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public Position2baseCallCount(final Position2baseCallCount src) {
		this();
		merge(src);
	}

	/**
	 * Adds Base at the provided positions.
	 * 
	 * @param position of the arrest where to add base
	 * @param base the base to be added
	 */
	public Position2baseCallCount addBaseCall(final int position, final Base base) {
		if (! pos2bcc.containsKey(position)) {
			pos2bcc.put(position, JACUSA.bccFactory.create());
		}
		pos2bcc.get(position).increment(base);
		return this;
	}
	
	public Position2baseCallCount addBaseCallCount(final int position, final BaseCallCount baseCallCount) {
		if (! pos2bcc.containsKey(position)) {
			pos2bcc.put(position, JACUSA.bccFactory.create());
		}
		pos2bcc.get(position).add(baseCallCount);
		return this;
	}
	
	public boolean contains(final int position) {
		return pos2bcc.containsKey(position);
	}

	/**
	 * Returns sorted positions
	 * 
	 * @return sorted positions
	 */
	public Set<Integer> getPositions() {
		return new TreeSet<Integer>(pos2bcc.keySet());
	}

	/**
	 * Returns base call count for position.
	 * 
	 * @param position of interest
	 * @return base call count at requested position;
	 */
	public BaseCallCount getBaseCallCount(final int position) {
		if (! contains(position)) {
			return new UnmodifiableBaseCallCount(EMPTY);
		}
		return new UnmodifiableBaseCallCount(pos2bcc.get(position));
	}
	
	public BaseCallCount getBaseCallCountDiff(final BaseCallCount baseCallCount, final int position) {
		final BaseCallCount bcc = baseCallCount.copy();
		bcc.subtract(getBaseCallCount(position));
		return bcc;
	}
	
	public BaseCallCount getBaseCallCountDiff(final BaseCallCount baseCallCount) {
		final BaseCallCount bcc = baseCallCount.copy();
		bcc.subtract(getTotalBaseCallCount());
		return bcc;
	}
	
	/**
	 * Returns base call count where position != pos (total - arrest = through)
	 * 
	 * @param refPos arrest position to be excluded
	 * @return base call count excluding arrest position pos
	 */
	/*
	public BaseCallCount getThroughBaseCallCount(final int refPos) {
		final BaseCallCount tmpTotalBcc = totalBcc.copy();
		if (contains(refPos)) {
			tmpTotalBcc.subtract(getBaseCallCount(refPos));
		}
		return new UnmodifiableBaseCallCount(tmpTotalBcc);
	}
	*/
	
	private BaseCallCount getTotalBaseCallCountHelper() {
		final BaseCallCount bcc = JACUSA.bccFactory.create();
		for (final BaseCallCount tmpBcc : pos2bcc.values()) {
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
	public void merge(final Position2baseCallCount src) {
		for (final int position : src.getPositions()) {
			if (! contains(position)) {
				pos2bcc.put(position, src.pos2bcc.get(position).copy());
			} else {
				pos2bcc.get(position)
					.add(src.pos2bcc.get(position));
			}
		}
	}
	
	/**
	 * Creates a deep copy
	 * 
	 * @return a deep copy
	 */
	@Override
	public Position2baseCallCount copy() {
		return new Position2baseCallCount(this);
	}
	
	/**
	 * Clears internal data structures
	 */
	public void clear() {
		pos2bcc.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof Position2baseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final Position2baseCallCount ap2bcc = (Position2baseCallCount)obj;
		return 
				pos2bcc.equals(ap2bcc.pos2bcc);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + pos2bcc.hashCode();
		return hash;
	}
	
	/**
	 * Pretty print of content
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("position\tBaseCallCount\n");
		for (final int position : getPositions()) {
			sb.append(position);
			sb.append("\t");
			sb.append(BaseCallCount.toString(getBaseCallCount(position)));
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public static class Parser implements lib.util.Parser<Position2baseCallCount> {
		
		public static final char POS_SEP = ',';
		public static final char FIELD_SEP = ':';
		public static final char EMPTY = '*';

		private final char posSep;
		private final char fieldSep;
		private final char empty;

		private final Pattern pattern;
		private final BaseCallCount.AbstractParser bccParser;
		
		public Parser() {
			this(POS_SEP,
					FIELD_SEP,
					EMPTY,
					new ArrayBaseCallCount.Parser() );
		}

		public Parser(final char posSep, final char fieldSep, final char empty, final BaseCallCount.AbstractParser bccParser) {
			this.posSep = posSep;
			this.fieldSep = fieldSep;
			this.empty = empty;

			pattern = Pattern.compile(
					"([0-9]+)" + 
					this.fieldSep + 
					"([^" + this.fieldSep +  this.posSep + "]+)");
			this.bccParser = bccParser;
		}
		
		@Override
		public Position2baseCallCount parse(String s) {
			final int i = s.indexOf(Character.toString(POS_SEP)); 
			if (i < 0) {
				throw new IllegalStateException("Current Position could not be parser");
			}
		
			final Position2baseCallCount o = new Position2baseCallCount();
			if (s.equals(Character.toString(empty))) {
				return o;
			}

			final int arrestPositions = s.split(Character.toString(posSep)).length;

			final Matcher match = pattern.matcher(s);
			while (match.find()) {
				final int pos = Integer.parseInt(match.group(1));
				if (pos <= 0) {
					throw new IllegalArgumentException("Position cannot be negative: " + pos);
				}
				final BaseCallCount bcc = bccParser.parse(match.group(2));
				if (o.pos2bcc.containsKey(pos)) {
	        		throw new IllegalStateException("Duplicate position: " + pos);
	        	}
	        	o.pos2bcc.put(pos, bcc);
	        }
			if (arrestPositions != o.getPositions().size()) {
	        	throw new IllegalArgumentException("Size of parsed sites does not match: " + s);
			}
			if (o.getPositions().size() == 0) {
				throw new IllegalArgumentException("Cannot parse arrest positions from: " + s);
			}
			return o;
		}
		
		@Override
		public String wrap(final Position2baseCallCount o) {
			if (o.getPositions().size() == 0) {
				return Character.toString(empty);
			}

			final List<String> strList = new ArrayList<>();
			for (final int arrestPos : o.getPositions()) {
				final BaseCallCount bcc = o.getBaseCallCount(arrestPos);
				strList.add(
						StringUtil.join(
								Character.toString(FIELD_SEP), 
								Arrays.asList(
										Integer.toString(arrestPos),
										bccParser.wrap(bcc))) );
			}

			return StringUtil.join(
					Character.toString(POS_SEP), strList);
		}
	}
	
}
