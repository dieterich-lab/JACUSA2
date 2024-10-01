package lib.util.coordinate;

import lib.util.coordinate.CoordinateUtil.STRAND;

/**
 * TODO add documentation
 */
abstract class AbstractCoordinate implements Coordinate {
	
	protected static final int DEFAULT_START 	= 0;
	protected static final int DEFAULT_END 		= -1;
	
	private static final long serialVersionUID = 1L;

	private String contig;
	// stored as 1-based
	private int start;
	private int end;
	
	private STRAND strand;

	protected AbstractCoordinate(final String contig, final int start, final int end, STRAND strand) {
		this.contig = contig;
		this.start 	= start;
		this.end 	= end;
		this.strand	= strand;
	}
	
	protected AbstractCoordinate() {
		this("", DEFAULT_START, DEFAULT_END, STRAND.UNKNOWN);
	}
	
	protected AbstractCoordinate(final AbstractCoordinate coordinate) {
		this(coordinate.contig, coordinate.start, coordinate.end, coordinate.strand);
	}
	
	protected AbstractCoordinate(final String contig, final int start, final int end) {
		this(contig, start, end, STRAND.UNKNOWN);
	}
	
	@Override
	public String getContig() {
		return contig;
	}

	@Override
	public int get0Start() {
		return start - 1;
	}
	
	@Override
	public int get1Start() {
		return start;
	}
	
	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int get0End() {
		return end;
	}
	
	@Override
	public int get1End() {
		return end;
	}
	
	@Override
	public int getEnd() {
		return get0End();
	}

	@Override
	public void setContig(String contig) {
		this.contig = contig;
	}

	@Override
	public void setStrand(STRAND strand) {
		this.strand = strand;
	}

	@Override
	public int get0Position() {
		return get0Start();
	}

	@Override
	public int get1Position() {
		return get1Start();
	}

	@Override
	public void set0Position(int zeroPosition) {
		set0Position(zeroPosition + 1);
	}
	
	@Override
	public void set1Position(int onePosition) {
		start 	= onePosition;
		end		= start;
	}
	
	@Override
	public void setStart(Coordinate coordinate) {
		start = coordinate.get1Start();
	}

	@Override
	public void setEnd(Coordinate coordinate) {
		end = coordinate.get1End();
	}

	@Override
	public void setPosition(Coordinate coordinate) {
		set1Position(coordinate.get1Position());
	}

	@Override
	public void setMaxPosition() {
		start 	= Integer.MAX_VALUE;
		end		= DEFAULT_END;
	}
	
	@Override
	public void resetPosition(Coordinate coordinate) {
		contig 	= coordinate.getContig();
		start 	= DEFAULT_START;
		end 	= DEFAULT_END;
		strand 	= coordinate.getStrand(); 	
	}
	
	@Override
	public int getLength() {
		return end - start + 1;
	}
	
	@Override
	public abstract String toString();
	
	protected String toString(final boolean oneBased, final int start, final int end) {
		return (oneBased ? "1" : "0") + 
				"-based: " + 
				contig + 
				AbstractParser.CONTIG_POS_SEP + 
				start + 
				AbstractParser.START_END_SEP + 
				end + 
				AbstractParser.POS_STRAND_SEP + 
				getStrand().character();
	}

	@Override
	public STRAND getStrand() {
		return strand;
	}

	@Override
	public boolean isReverseStrand() {
		return strand == STRAND.REVERSE;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof AbstractCoordinate)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final AbstractCoordinate coordinate = (AbstractCoordinate)obj;
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
	
}
