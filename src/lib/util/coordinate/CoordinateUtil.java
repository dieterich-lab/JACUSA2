package lib.util.coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class CoordinateUtil {

	public static final char STRAND_FORWARD_CHAR = '+';
	public static final char STRAND_REVERSE_CHAR = '-';
	public static final char STRAND_UNKNOWN_CHAR = '.';

	public static boolean isContained(final Coordinate coordinate, final int position) {
		return coordinate.getStart() <= position && coordinate.getEnd() >= position;
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
	
	public enum STRAND {
		FORWARD(STRAND_FORWARD_CHAR),REVERSE(STRAND_REVERSE_CHAR),UNKNOWN(STRAND_UNKNOWN_CHAR);
		
		final char c;

		private STRAND(final char c) {
			this.c = c;
		}
		
		public final char character() {
	        return c;
	    }

	}
	
}
