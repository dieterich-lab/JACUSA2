package lib.util.coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class Coordinate {

	public static final char STRAND_FORWARD_CHAR = '+';
	public static final char STRAND_REVERSE_CHAR = '-';
	public static final char STRAND_UNKNOWN_CHAR = '.';

	private String contig;
	private int start;
	private int end;
	private STRAND strand;

	public Coordinate() {
		contig 	= new String();
		start 	= -1;
		end 	= -1;
		strand 	= STRAND.UNKNOWN;
	}

	public Coordinate(final Coordinate coordinate) {
		contig 	= new String(coordinate.contig);
		start 	= coordinate.start;
		end 	= coordinate.end;
		strand	= coordinate.strand;
	}
	
	public Coordinate(final String contig, 
			final int start) {
		this(contig, start, start, STRAND.UNKNOWN);
	}
	
	public Coordinate(final String contig, 
			final int start, final int end) {
		this(contig, start, end, STRAND.UNKNOWN);
	}
	
	public Coordinate(final String contig, 
			final int start, final STRAND strand) {
		this(contig, start, start, strand);
	}
	
	public Coordinate(final String contig, 
			final int start, final int end, STRAND strand) {
		this.contig = contig;
		this.start 	= start;
		this.end 	= end;
		this.strand	= strand;
	}
	
	public String getContig() {
		return contig;
	}

	public void setContig(final String contig) {
		this.contig = contig;
	}

	public int getStart() {
		return start;
	}

	public void setPosition(final int position) {
		start = position;
		end = position;
	}

	public int getPosition() {
		return start;
	}
	
	public void setStart(final int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(final int end) {
		this.end = end;
	}
	
	public String toString() {
		return contig + "_" + start + "-" + end + "_" + getStrand().toString();
	}
	
	public STRAND getStrand() {
		return strand;
	}
	
	public void setStrand(final STRAND strand) {
		this.strand = strand;
	}
	
	public static boolean isContained(final Coordinate coordinate, final int position) {
		return coordinate.getStart() <= position && coordinate.getEnd() >= position;
	}
	
	public static int makeRelativePosition(final Coordinate coordinate, final int position) {
		if(position > coordinate.getEnd() || position < coordinate.getStart()){
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
		
		private final char c;
		
		private STRAND(char c) {
			this.c = c;
		}

		public final char character() {
	        return c;
	    }

	}
	
}
