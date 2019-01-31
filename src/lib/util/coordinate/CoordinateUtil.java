package lib.util.coordinate;

import java.io.Serializable;

/**
 *
 */
public final class CoordinateUtil {

	public static final char STRAND_FORWARD_CHAR = '+';
	public static final char STRAND_REVERSE_CHAR = '-';
	public static final char STRAND_UNKNOWN_CHAR = '.';

	private CoordinateUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Tested in @see test.lib.util.coordinate.CoordinateUtil#testIsContained
	 */
	public static boolean isContained(final Coordinate coordinate, final int position) {
		return coordinate.getStart() <= position && coordinate.getEnd() >= position;
	}
	
	/**
	 * 	MAX => no matching contigs
	 * 	 -1 => coord2 coord1
	 * 	  0 => coord1 and coord2 overlap
	 * 	 +1 => coord1 coord2
	 * @param coord1
	 * @param coord2
	 * @return
	 * 
	 * Tested in @see test.lib.util.coordinate.CoordinateUtil#testOrientation 
	 */
	public static int orientation(final Coordinate coord1, final Coordinate coord2) {
		if (! coord1.getContig().equals(coord2.getContig())) {
			return Integer.MAX_VALUE;
		}

		final int start1 	= coord1.getStart();
		final int end1 		= coord1.getEnd();
		final int start2 	= coord2.getStart();
		final int end2 		= coord2.getEnd();

		if (start2 > end1) {
			return 1;
		} else if (end2 < start1){
			return -1;
		} else {
			return 0;
		}
	}
	
	public static int makeRelativePosition(final Coordinate coordinate, final int position) {
		if (position > coordinate.getEnd() || position < coordinate.getStart()){
			return -1;
		}
		return position - coordinate.getStart();
	}
	
	public static STRAND invertStrand(final STRAND strand) {
		switch (strand) {
		case FORWARD:
			return STRAND.REVERSE;

		case REVERSE:
			return STRAND.FORWARD;

		case UNKNOWN:
			return STRAND.UNKNOWN;
		}

		return STRAND.UNKNOWN;
	}
	
	public enum STRAND implements Serializable {
		FORWARD(STRAND_FORWARD_CHAR),REVERSE(STRAND_REVERSE_CHAR),UNKNOWN(STRAND_UNKNOWN_CHAR);
		
		private final char c;

		private STRAND(final char c) {
			this.c = c;
		}
		
		public static STRAND valueOf(final char c) {
			switch (c) {
			case STRAND_FORWARD_CHAR:
					return STRAND.FORWARD;
				
			case STRAND_REVERSE_CHAR:
					return STRAND.REVERSE;
			
			case STRAND_UNKNOWN_CHAR:
					return STRAND.UNKNOWN;
			
			default:
				throw new EnumConstantNotPresentException(STRAND.class, Character.toString(c));
			}
		}

		public final char character() {
	        return c;
	    }

	}
	
	public static Coordinate mergeCoordinate(final Coordinate c1, final Coordinate c2) {
		if (! c1.equals(c2)) {
			throw new IllegalStateException("data1 and data2 have different coordinates: " + c1.toString() + " != " + c2.toString());
		}
		if (! c1.overlaps(c2)) {
			return null;
		}
		
		return new OneCoordinate(
				c1.getContig(),
				Math.min(c1.get1Start(), c2.get1Start()),
				Math.max(c1.get1End(), c2.get1End()),
				c1.getStrand() == c2.getStrand() ? c1.getStrand() : STRAND.UNKNOWN);
	}
	
}
