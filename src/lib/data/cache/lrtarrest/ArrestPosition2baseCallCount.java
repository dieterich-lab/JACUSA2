package lib.data.cache.lrtarrest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class ArrestPosition2baseCallCount 
implements Serializable, Data<ArrestPosition2baseCallCount> {
	
	private static final long serialVersionUID = 1L;
	
	private static final BaseCallCount EMPTY = JACUSA.bccFactory.create();
	
	/**
	 * stores base call count information for each position
	 */
	private Map<Integer, BaseCallCount> aPos2bcc;
	
	// cached arrest positions
	private List<Integer> cApos;
	private BaseCallCount cTotBcc;
	
	public ArrestPosition2baseCallCount() {
		aPos2bcc = new HashMap<Integer, BaseCallCount>(5);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public ArrestPosition2baseCallCount(final ArrestPosition2baseCallCount src) {
		this();
		merge(src);
	}

	/**
	 * Adds Base at the provided positions.
	 * 
	 * @param arrestPosition of the arrest where to add base
	 * @param base the base to be added
	 */
	public ArrestPosition2baseCallCount addBaseCall(final int arrestPosition, final Base base) {
		if (! aPos2bcc.containsKey(arrestPosition)) {
			aPos2bcc.put(arrestPosition, JACUSA.bccFactory.create());
		}
		aPos2bcc.get(arrestPosition).increment(base);
		return this;
	}
	
	public boolean contains(final int arrestPosition) {
		return aPos2bcc.containsKey(arrestPosition);
	}

	/**
	 * Returns positions
	 * 
	 * @return positions
	 */
	public List<Integer> getPositions() {
		if (cApos == null) {
			cApos = new ArrayList<>(new TreeSet<>(aPos2bcc.keySet()));
		}
		return cApos;
	}

	/**
	 * Returns base call count for position.
	 * 
	 * @param referencePosition of interest
	 * @return base call count at requested position;
	 */
	public BaseCallCount getArrestBaseCallCount(final int referencePosition) {
		if (! contains(referencePosition)) {
			return new UnmodifiableBaseCallCount(EMPTY);
		}
		return new UnmodifiableBaseCallCount(aPos2bcc.get(referencePosition));
	}
	
	/**
	 * Returns base call count where position != pos (total - arrest = through)
	 * 
	 * @param refPos arrest position to be excluded
	 * @return base call count excluding arrest position pos
	 */
	public BaseCallCount getThroughBaseCallCount(final int refPos) {
		final BaseCallCount tmpTotalBcc = getTotalBaseCallCountHelper();
		if (contains(refPos)) {
			tmpTotalBcc.subtract(getArrestBaseCallCount(refPos));
		}
		return new UnmodifiableBaseCallCount(tmpTotalBcc);
	}

	private BaseCallCount getTotalBaseCallCountHelper() {
		if (cTotBcc == null) {
			cTotBcc = JACUSA.bccFactory.create();
			for (final BaseCallCount tmpBcc : aPos2bcc.values()) {
				cTotBcc.add(tmpBcc);
			}
		}
		return cTotBcc;
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
	public void merge(final ArrestPosition2baseCallCount src) {
		for (final int position : src.getPositions()) {
			if (! contains(position)) {
				aPos2bcc.put(position, src.aPos2bcc.get(position).copy());
			} else {
				aPos2bcc.get(position)
					.add(src.aPos2bcc.get(position));
			}
		}
		
		if (cTotBcc != null && src.cTotBcc != null) {
			cTotBcc.merge(src.cTotBcc);
		} else {
			cTotBcc = null;
		}
		
		if (cApos != null) {
			cApos = null;
		}
	}
	
	/**
	 * Creates a deep copy
	 * 
	 * @return a deep copy
	 */
	@Override
	public ArrestPosition2baseCallCount copy() {
		return new ArrestPosition2baseCallCount(this);
	}
	
	/**
	 * Clears internal data structures
	 */
	public void clear() {
		aPos2bcc.clear();
		cApos = null;
		cTotBcc = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof ArrestPosition2baseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final ArrestPosition2baseCallCount ap2bcc = (ArrestPosition2baseCallCount)obj;
		return 
				aPos2bcc.equals(ap2bcc.aPos2bcc);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + aPos2bcc.hashCode();
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
			sb.append("\t\t");
			sb.append(BaseCallCount.toString(getArrestBaseCallCount(position)));
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public static class Parser implements lib.util.Parser<ArrestPosition2baseCallCount> {
		
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
		public ArrestPosition2baseCallCount parse(String s) {
			final int i = s.indexOf(Character.toString(POS_SEP)); 
			if (i < 0) {
				throw new IllegalStateException("Current Position could not be parser");
			}
		
			final ArrestPosition2baseCallCount o = new ArrestPosition2baseCallCount();
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
				if (o.aPos2bcc.containsKey(pos)) {
	        		throw new IllegalStateException("Duplicate position: " + pos);
	        	}
	        	o.aPos2bcc.put(pos, bcc);
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
		public String wrap(final ArrestPosition2baseCallCount o) {
			if (o.getPositions().size() == 0) {
				return Character.toString(empty);
			}

			final List<String> strList = new ArrayList<>();
			for (final int arrestPos : o.getPositions()) {
				final BaseCallCount bcc = o.getArrestBaseCallCount(arrestPos);
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
