package lib.util.coordinate;

import lib.util.coordinate.CoordinateUtil.STRAND;

public class ZeroCoordinate extends AbstractCoordinate {
	
	private static final long serialVersionUID = 1L;

	public ZeroCoordinate(final String contig, final int start, final int end, STRAND strand) {
		super(contig, start, end, strand);
	}
	
	public ZeroCoordinate() {
		super();
	}
	
	public ZeroCoordinate(final ZeroCoordinate coordinate) {
		super(coordinate);
	}
	
	public ZeroCoordinate(final String contig, final int start, final int end) {
		super(contig, start, end);
	}
	
	public ZeroCoordinate(final String contig, final int position, STRAND strand) {
		super(contig, position, position + 1, strand);
	}

	@Override
	public Coordinate copy() {
		return new ZeroCoordinate(this);
	}

	@Override
	public String toString() {
		return toString(false, get0Start(), get0End());
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
			return new ZeroCoordinate(contig, start, end, strand);
		}
		
	}

	
}
