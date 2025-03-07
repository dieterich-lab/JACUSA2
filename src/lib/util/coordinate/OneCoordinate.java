package lib.util.coordinate;

import lib.util.coordinate.CoordinateUtil.STRAND;

/**
 * TODO add documentation
 */
public class OneCoordinate extends AbstractCoordinate {
	
	private static final long serialVersionUID = 1L;

	public OneCoordinate(final String contig, final int start, final int end, STRAND strand) {
		super(contig, start, end, strand);
	}
	
	public OneCoordinate() {
		super();
	}
	
	public OneCoordinate(final OneCoordinate coordinate) {
		super(coordinate);
	}
	
	public OneCoordinate(final String contig, final int start, final int end) {
		super(contig, start, end);
	}
	
	public OneCoordinate(final String contig, final int position, STRAND strand) {
		super(contig, position, position, strand);
	}

	@Override
	public Coordinate copy() {
		return new OneCoordinate(this);
	}
	
	@Override
	public String toString() {
		return toString(true, get1Start(), get1End());
	}

	public static class Parser extends Coordinate.AbstractParser {
		
		public Parser() {
			super();
		}
		
		public Parser(final char sepContigPos, final char sepStartEnd, final char sepPosStrand) {
			super(sepContigPos, sepStartEnd, sepPosStrand);
		}
		
		@Override
		public Coordinate create(String contig, int start, int end, STRAND strand) {
			return new OneCoordinate(contig, start, end, strand);
		}
		
	}
	
}
