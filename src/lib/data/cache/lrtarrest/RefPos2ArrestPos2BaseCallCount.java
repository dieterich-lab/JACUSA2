package lib.data.cache.lrtarrest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.StringUtil;
import jacusa.JACUSA;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBaseCallCount;
import lib.util.Base;
import lib.util.Copyable;
import lib.util.Mergeable;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.DefaultCoordinateTranslator;

public class RefPos2ArrestPos2BaseCallCount 
implements Serializable, Copyable<RefPos2ArrestPos2BaseCallCount>, Mergeable<RefPos2ArrestPos2BaseCallCount> {

	private static final long serialVersionUID = 1L;

	private final CoordinateTranslator coordinateTranslator;
	
	private ArrestPos2BaseCallCount[] win2arrest2bcc;
	private Map<Integer, ArrestPos2BaseCallCount> ref2arrest2bcc;

	/**
	 * Default constructor
	 */
	public RefPos2ArrestPos2BaseCallCount(final CoordinateTranslator coordinateTranslator) {
		this.coordinateTranslator = coordinateTranslator;
		
		win2arrest2bcc = new ArrestPos2BaseCallCount[coordinateTranslator.getLength()];
		ref2arrest2bcc = new HashMap<>(100);
	}
	
	

	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public RefPos2ArrestPos2BaseCallCount(final RefPos2ArrestPos2BaseCallCount src) {
		this(src.coordinateTranslator);
		merge(src);
	}

	/**
	 * TODO add comments
	 * @param refPos
	 * @param arrestPos
	 * @param base
	 * @param window
	 * @return
	 */
	// could be changed
	public RefPos2ArrestPos2BaseCallCount addBaseCall(
			final int refPos, final int arrestPos, 
			final Base base, 
			final Coordinate window) {

		final int winPos = coordinateTranslator.convert2windowPosition(refPos);
		if (winPos < 0) {
			if (! ref2arrest2bcc.containsKey(refPos)) {
				final ArrestPos2BaseCallCount ap2bcc = new ArrestPos2BaseCallCount();
				ref2arrest2bcc.put(refPos, ap2bcc);
			}
			ref2arrest2bcc.get(refPos).addBaseCall(arrestPos, base, window);
		} else {
			if (win2arrest2bcc[winPos] == null) {
				final ArrestPos2BaseCallCount ap2bcc = new ArrestPos2BaseCallCount();
				win2arrest2bcc[winPos] = ap2bcc;
			}
			win2arrest2bcc[winPos].addBaseCall(arrestPos, base, window);
		}

		return this;
	}

	// could be changed
	public ArrestPos2BaseCallCount getArrestPos2BaseCallCount(
			final int refPos) {
		final int winPos = coordinateTranslator.convert2windowPosition(refPos);
		if (winPos >= 0) {
			return win2arrest2bcc[winPos];
		}
		return ref2arrest2bcc.get(refPos);
	}
	
	/**
	 * TODO add comments
	 * @param refPos
	 * @return
	 */
	public boolean contains(final int refPos) {
		final int winPos = coordinateTranslator.convert2windowPosition(refPos);
		if (winPos >= 0) {
			return win2arrest2bcc[winPos] != null;
		}
		return ref2arrest2bcc.containsKey(refPos);
	}

	/**
	 * TODO add comments
	 * @return
	 */
	public Set<Integer> getRefPos() {
		final Set<Integer> refPositions = new TreeSet<>();
		for (int winPos = 0; winPos < win2arrest2bcc.length; winPos++) {
			if (win2arrest2bcc[winPos] != null) {
				final int refPosition = coordinateTranslator.convert2referencePosition(winPos);
				refPositions.add(refPosition);
			}
		}
		if (ref2arrest2bcc.keySet().size() > 0) {
			refPositions.addAll(ref2arrest2bcc.keySet());
		}
		return Collections.unmodifiableSet(refPositions);
	}
	
	/**
	 * Returns base call count for reference position.
	 * 
	 * @param refPos reference position of interest
	 * @return base call count at requested reference position;
	 */
	public BaseCallCount getTotalBaseCallCount(
			final int refPos) {
		final int winPos = coordinateTranslator.convert2windowPosition(refPos);
		final BaseCallCount totalBaseCallCount = JACUSA.bccFactory.create();
		if (winPos >= 0) {
			totalBaseCallCount.add(win2arrest2bcc[winPos].getTotalBaseCallCount());
		}
		if (ref2arrest2bcc.containsKey(refPos)) {
			totalBaseCallCount.add(ref2arrest2bcc.get(refPos).getTotalBaseCallCount());
		}
		return new UnmodifiableBaseCallCount(totalBaseCallCount);
	}

	/**
	 * Adds arrest positions and corresponding base call counts 
	 * 
	 * @param src ArrestPos2BaseCallCount to be added
	 */
	@Override
	public void merge(final RefPos2ArrestPos2BaseCallCount src) {
		if (! coordinateTranslator.equals(src.coordinateTranslator)) {
			throw new IllegalStateException();
		}
		
		for (int winPos = 0; winPos < src.win2arrest2bcc.length; winPos++) {
			if (src.win2arrest2bcc[winPos] != null) {
				if (win2arrest2bcc[winPos] == null) {
					final ArrestPos2BaseCallCount ap2bcc = new ArrestPos2BaseCallCount();
					win2arrest2bcc[winPos] = ap2bcc;
				}
				win2arrest2bcc[winPos].merge(src.win2arrest2bcc[winPos]);
			}
		}
		for (final int refPos : src.ref2arrest2bcc.keySet()) {
			if (! ref2arrest2bcc.containsKey(refPos)) {
				final ArrestPos2BaseCallCount ap2bcc = new ArrestPos2BaseCallCount();
				ref2arrest2bcc.put(refPos, ap2bcc);
			}
			final ArrestPos2BaseCallCount ap2bcc = ref2arrest2bcc.get(refPos);
			ap2bcc.merge(src.ref2arrest2bcc.get(refPos));
		}
	}
	
	/**
	 * Creates a deep copy
	 * 
	 * @return a deep copy
	 */
	@Override
	public RefPos2ArrestPos2BaseCallCount copy() {
		return new RefPos2ArrestPos2BaseCallCount(this);
	}
	
	/**
	 * Resets internal data structures
	 */
	public void reset() {
		Arrays.fill(win2arrest2bcc, null);
		ref2arrest2bcc.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof RefPos2ArrestPos2BaseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final RefPos2ArrestPos2BaseCallCount refPos2ap2bcc = (RefPos2ArrestPos2BaseCallCount)obj;
		return win2arrest2bcc.equals(refPos2ap2bcc.win2arrest2bcc) && 
				ref2arrest2bcc.equals(refPos2ap2bcc.ref2arrest2bcc);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
        hash = 31 * hash + ref2arrest2bcc.hashCode();
        hash = 31 * hash  + win2arrest2bcc.hashCode();
		return hash;
	}
	
	/**
	 * Pretty print of content
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Stored ");
		sb.append(getRefPos().size());
		sb.append(" Positions");
		return sb.toString();
	}

	public static class Parser implements lib.util.Parser<RefPos2ArrestPos2BaseCallCount> {
		
		public static final char REF_POS_SEP = ' ';
		public static final char REF_POS_SEP2 = '=';
		public static final char EMPTY = '*';
		
		private final ArrestPos2BaseCallCount.Parser parser;
		
		private final char refPosSep;
		private final char refPosSep2;
		private final char empty;
		
		private final Pattern pattern;
		
		public Parser(
				final char refPosSep, 
				final char refPosSep2, 
				final char empty) {

			parser = new ArrestPos2BaseCallCount.Parser();
			this.refPosSep = refPosSep;
			this.refPosSep2 = refPosSep2;
			this.empty = empty;
			
			pattern = Pattern.compile(
					"([0-9]+)" + 
					refPosSep2 + 
					"([^" + refPosSep +  refPosSep2 + "]+)");
		}
		
		public Parser() {
			this(REF_POS_SEP, REF_POS_SEP2, EMPTY);
		}

		@Override
		public RefPos2ArrestPos2BaseCallCount parse(final String s) {
			if (s.equals(Character.toString(empty))) {
				throw new IllegalStateException();
			}

			// FIXME
			final RefPos2ArrestPos2BaseCallCount o = 
					new RefPos2ArrestPos2BaseCallCount(
							new DefaultCoordinateTranslator(-1, -1));
			
			final int positions = s.split(Character.toString(refPosSep)).length;

			final Matcher match = pattern.matcher(s);
			while (match.find()) {
				final int refPos = Integer.parseInt(match.group(1));
				if (refPos <= 0) {
					throw new IllegalArgumentException("Reference Position cannot be negative: " + refPos);
				}
				final ArrestPos2BaseCallCount ap2bcc = parser.parse(match.group(2));
				if (o.contains(refPos)) {
	        		throw new IllegalStateException("Duplicate Reference Position: " + refPos);
	        	}
	        	o.ref2arrest2bcc.put(refPos, ap2bcc);
	        }
			if (positions != o.ref2arrest2bcc.size()) {
	        	throw new IllegalArgumentException("Size of parsed sites does not match: " + s);
			}
			if (o.ref2arrest2bcc.size() == 0) {
				throw new IllegalArgumentException("Cannot parse Reference Positions from: " + s);
			}
			return o;
		}
		
		@Override
		public String wrap(RefPos2ArrestPos2BaseCallCount o) {
			final Set<Integer> refPositions = o.getRefPos();
			if (refPositions.size() == 0) {
				return Character.toString(empty);
			}
			final List<String> s = new ArrayList<>(refPositions.size()); 
			for (final int refPos : refPositions) {
				final ArrestPos2BaseCallCount ap2bcc = o.getArrestPos2BaseCallCount(refPos);
				final String tmp = 
						Integer.toString(refPos) + 
						Character.toString(refPosSep2) +
						parser.wrap(ap2bcc);
				s.add(tmp);
			}
			return StringUtil.join(
					Character.toString(refPosSep), 
					s); 
		}
		
	}
	
}
