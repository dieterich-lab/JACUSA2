package lib.util.coordinate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.Locatable;
import lib.util.Copyable;
import lib.util.coordinate.CoordinateUtil.STRAND;

/**
 *
 */
public interface Coordinate extends Locatable, Serializable, Copyable<Coordinate> {

	int get0Position();
	int get1Position();

	int get0Start();
	int get1Start();
	
	int get0End();
	int get1End();
	
	/**
	 * @param contig the contig to set
	 */
	void setContig(String contig);

	/**
	 * @param start the start to set
	 */
	void setStart(Coordinate coordinate);

	/**
	 * @param end the end to set
	 */
	void setEnd(Coordinate coordinate);

	/**
	 * @param position the position to set
	 */
	void set0Position(int zeroPosition);
	void set1Position(int onePosition);
	void setPosition(Coordinate coordinate);
	
	void setMaxPosition();
	
	public void resetPosition(Coordinate coordinate);
	
	/**
	 * @param strand the strand to set
	 */
	void setStrand(STRAND strand);
	
	STRAND getStrand();

	int getLength();

	// has JUNIT tests
	public static abstract class AbstractParser implements lib.util.Parser<Coordinate> {

		public static final char CONTIG_POS_SEP = ':';
		public static final char START_END_SEP 	= '-';
		public static final char POS_STRAND_SEP = ':';
		
		private final char contigPosSep;
		private final char startEndSep;
		private final char posStrandSep;
		
		public AbstractParser() {
			this(CONTIG_POS_SEP, START_END_SEP, POS_STRAND_SEP);
		}
		
		public AbstractParser(final char sepContigPos, final char sepStartEnd, final char sepPosStrand) {
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
	        return create(contig, start, end, strand);
		}
		
		public abstract Coordinate create(
				String contig, int start, int end, STRAND strand);
		
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
