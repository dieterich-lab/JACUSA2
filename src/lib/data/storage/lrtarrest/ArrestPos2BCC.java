package lib.data.storage.lrtarrest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.StringUtil;
import lib.data.Data;
import lib.data.count.basecall.ArrayBCC;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBCC;
import lib.util.Base;

/**
 * TODO add documentation
 */
public class ArrestPos2BCC 
implements Serializable, Data<ArrestPos2BCC> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * stores base call count information for each position
	 */
	private int refPos;

	private Map<Integer, BaseCallCount> aPos2bcc;
	private BaseCallCount tBcc;
	
	// cached arrest positions
	private List<Integer> cApos;
	private BaseCallCount cTotBcc;
	
	public ArrestPos2BCC() {
		this(-1);
	}
	
	public ArrestPos2BCC(final int refPos) {
		this.refPos = refPos;
		
		aPos2bcc 	= new HashMap<Integer, BaseCallCount>();
		tBcc		= BaseCallCount.create();
	}
	
	public void setReferencePosition(final int refPos) {
		this.refPos = refPos;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public ArrestPos2BCC(final ArrestPos2BCC src) {
		this(-1);
		merge(src);
	}

	public ArrestPos2BCC addBaseCall(final Base base) {
		tBcc.increment(base);
		return this;
	}
	
	/**
	 * Adds Base at the provided positions.
	 * 
	 * @param arrestPosition of the arrest where to add base
	 * @param base the base to be added
	 */
	public ArrestPos2BCC addBaseCall(final int arrestPosition, final Base base) {
		if (! aPos2bcc.containsKey(arrestPosition)) {
			aPos2bcc.put(arrestPosition, BaseCallCount.create());
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
			cApos = Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(aPos2bcc.keySet())));
		}
		return cApos;
	}

	/**
	 * Returns base call count for position.
	 * 
	 * @param arrestPos of interest
	 * @return base call count at requested position;
	 */
	public BaseCallCount getArrestBCC(final int arrestPos) {
		if (! contains(arrestPos)) {
			return BaseCallCount.EMPTY;
		}
		return new UnmodifiableBCC(aPos2bcc.get(arrestPos));
	}
	
	/**
	 * Returns base call count where position != pos (total - arrest = through)
	 * 
	 * @param arrestPos arrest position to be excluded
	 * @return base call count excluding arrest position pos
	 */
	public BaseCallCount getThroughBCC(final int arrestPos) {
		final BaseCallCount tmpTotalBcc = BaseCallCount.create();
		tmpTotalBcc.add(getTotalBaseCallCountHelper());
		if (contains(arrestPos)) {
			tmpTotalBcc.subtract(getArrestBCC(arrestPos));
		}
		if (arrestPos != refPos) {
			tmpTotalBcc.subtract(getArrestBCC(refPos));
		}
		return new UnmodifiableBCC(tmpTotalBcc);
	}

	private BaseCallCount getTotalBaseCallCountHelper() {
		if (cTotBcc == null) {
			final BaseCallCount tmpTotBcc = BaseCallCount.create();
			for (final BaseCallCount tmpBcc : aPos2bcc.values()) {
				tmpTotBcc.add(tmpBcc);
			}
			tmpTotBcc.add(tBcc);
			cTotBcc = new UnmodifiableBCC(tmpTotBcc);
		}
		return cTotBcc;
	}
	
	/**
	 * Returns total base call count, including base call counts from all arrest positions
	 * 
	 * @return base call count, summed over all arrest positions
	 */
	public BaseCallCount getTotalBCC() {
		return getTotalBaseCallCountHelper();
	}

	/**
	 * Adds arrest positions and corresponding base call counts 
	 * 
	 * @param src ArrestPos2BaseCallCount to be added
	 */
	@Override
	public void merge(final ArrestPos2BCC src) {
		this.refPos = src.refPos;
		
		for (final int position : src.getPositions()) {
			if (! contains(position)) {
				aPos2bcc.put(position, src.aPos2bcc.get(position).copy());
			} else {
				aPos2bcc.get(position).add(src.aPos2bcc.get(position));
			}
		}
		tBcc.add(src.tBcc);
		
		if (cTotBcc != null && src.cTotBcc != null) {
			cTotBcc.merge(src.cTotBcc);
		} else {
			cTotBcc = null;
		}
		
		cApos = null;
	}
	
	/**
	 * Creates a deep copy
	 * 
	 * @return a deep copy
	 */
	@Override
	public ArrestPos2BCC copy() {
		return new ArrestPos2BCC(this);
	}
	
	/**
	 * Clears internal data structures
	 */
	public void clear() {
		if (aPos2bcc.size() > 0) {
			aPos2bcc.clear();
			cApos 	= null;
			cTotBcc = null;
			tBcc.clear();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof ArrestPos2BCC)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final ArrestPos2BCC ap2bcc = (ArrestPos2BCC)obj;
		return aPos2bcc.equals(ap2bcc.aPos2bcc);
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
			sb.append(BaseCallCount.toString(getArrestBCC(position)));
			sb.append('\n');
		}
		return sb.toString();
	}

	public static class Parser implements lib.util.Parser<ArrestPos2BCC> {
		
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
					new ArrayBCC.Parser() );
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
		public ArrestPos2BCC parse(String s) {
			final ArrestPos2BCC o = new ArrestPos2BCC(-1);
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
			if (o.getPositions().isEmpty()) {
				throw new IllegalArgumentException("Cannot parse arrest positions from: " + s);
			}
			return o;
		}
		
		@Override
		public String wrap(final ArrestPos2BCC o) {
			if (o.getPositions().isEmpty()) {
				return Character.toString(empty);
			}

			final List<String> strList = new ArrayList<>();
			for (final int arrestPos : o.getPositions()) {
				final BaseCallCount bcc = o.getArrestBCC(arrestPos);
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
