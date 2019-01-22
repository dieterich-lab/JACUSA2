package lib.util.coordinate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.Locatable;
import lib.util.coordinate.CoordinateUtil.STRAND;

/**
 *
 */
public class Coordinate implements Locatable, Serializable {
	
	private static final long serialVersionUID = 1L;

	private String contig;
	private int start;
	private int end;
	private STRAND strand;

	public Coordinate(final String contig, final int start, final int end, STRAND strand) {
		this.contig = contig;
		this.start 	= start;
		this.end 	= end;
		this.strand	= strand;
	}
	
	public Coordinate() {
		this(new String(), -1, -1, STRAND.UNKNOWN);
	}
	
	public Coordinate(final Coordinate coordinate) {
		this(coordinate.contig, coordinate.start, coordinate.end, coordinate.strand);
	}
	
	public Coordinate(final String contig, final int start, final int end) {
		this(contig, start, end, STRAND.UNKNOWN);
	}
	
	public Coordinate(final String contig, final int position, STRAND strand) {
		this(contig, position, position, strand);
	}
	
	@Override
	public String getContig() {
		return contig;
	}

	@Override
	public int getStart() {
		return start;
	}

	public int getPosition() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	/**
	 * @param contig the contig to set
	 */
	public void setContig(String contig) {
		this.contig = contig;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.start = position;
		this.end = position + 1; // TODO test
	}
	
	/**
	 * @param strand the strand to set
	 */
	public void setStrand(STRAND strand) {
		this.strand = strand;
	}

	@Override
	public String toString() {
		return contig + 
				Parser.CONTIG_POS_SEP + 
				start + 
				Parser.START_END_SEP + 
				end + 
				Parser.POS_STRAND_SEP + 
				getStrand().character();
	}
	
	public STRAND getStrand() {
		return strand;
	}


	public int getLength() {
		return end - start + 1;
	}

	public void clear() {
		contig = new String();
		start = -1;
		end = -1;
		strand = STRAND.UNKNOWN;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof Coordinate)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final Coordinate coordinate = (Coordinate)obj;
		return 
				contig.equals(coordinate.contig) && 
				start == coordinate.start &&
				end == coordinate.end &&
				strand.equals(coordinate.strand);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + contig.hashCode();
		hash = 31 * hash + start;
		hash = 31 * hash + end;
		hash = 31 * hash + strand.hashCode();
		return hash;
	}

	// has JUNIT tests
	public static class Parser implements lib.util.Parser<Coordinate> {

		public static final char CONTIG_POS_SEP = ':';
		public static final char START_END_SEP 	= '-';
		public static final char POS_STRAND_SEP = ':';
		
		private final char contigPosSep;
		private final char startEndSep;
		private final char posStrandSep;
		
		public Parser() {
			this(CONTIG_POS_SEP, START_END_SEP, POS_STRAND_SEP);
		}
		
		public Parser(final char sepContigPos, final char sepStartEnd, final char sepPosStrand) {
			this.contigPosSep 	= sepContigPos;
			this.startEndSep 	= sepStartEnd;
			this.posStrandSep 	= sepPosStrand;
		}

		@Override
		public Coordinate parse(final String s) {
			if (s == null) {
				throw new IllegalArgumentException("s cannot be null");
			}

			final char F = STRAND.FORWARD.character();
			final char R = STRAND.REVERSE.character();
			final char U = STRAND.UNKNOWN.character();

			// expected format chr:start-end:strand -1 0 +1
			final Pattern pattern = Pattern.compile("([^" + contigPosSep + startEndSep + posStrandSep + "]+)" + contigPosSep + "(\\d+)" + startEndSep + "(\\d+)" + posStrandSep +
					"("+ R + "|\\"+ U + "|\\" + F +")");

			final Matcher match = pattern.matcher(s);
	        if (! match.find()) {
	        	throw new IllegalArgumentException("Cannot parse: " + s + "\n" +  
	        			"Expected format: contig:start-end:strand("+ R + "|"+ U + "|" + F +")" );
	        }
	        final String contig = match.group(1);
	        final int start 	= Integer.parseInt(match.group(2));
	        final int end 		= Integer.parseInt(match.group(3));
	        final STRAND strand = STRAND.valueOf(match.group(4).charAt(0));
	        return new Coordinate(contig, start, end, strand);
		}
		
		@Override
		public String wrap(Coordinate coordinate) {
			return coordinate.getContig() + 
					contigPosSep + 
					coordinate.getStart() + 
					startEndSep + 
					coordinate.getEnd() + 
					posStrandSep + 
					coordinate.getStrand().character();
		}
		
	}
	
}
