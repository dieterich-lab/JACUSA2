package lib.util.coordinate;

import lib.util.coordinate.CoordinateUtil.STRAND;

public class UnmodifiableCoordinate implements Coordinate {

	private static final long serialVersionUID = 1L;
	
	private final Coordinate coordinate;
	
	public UnmodifiableCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public String getContig() {
		return coordinate.getContig();
	}

	@Override
	public int getStart() {
		return coordinate.getStart();
	}

	@Override
	public int getEnd() {
		return coordinate.getEnd();
	}

	@Override
	public Coordinate copy() {
		return new UnmodifiableCoordinate(coordinate);
	}

	@Override
	public int get0Position() {
		return coordinate.get0Position();
	}

	@Override
	public int get1Position() {
		return coordinate.get1Position();
	}

	@Override
	public int get0Start() {
		return coordinate.get0Start();
	}

	@Override
	public int get1Start() {
		return coordinate.get1Start();
	}

	@Override
	public int get0End() {
		return coordinate.get0End();
	}

	@Override
	public int get1End() {
		return coordinate.get1End();
	}

	@Override
	public void setContig(String contig) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStart(Coordinate coordinate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEnd(Coordinate coordinate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set0Position(int zeroPosition) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void set1Position(int onePosition) {
		throw new UnsupportedOperationException();	
	}
	
	@Override
	public void setMaxPosition() {
		throw new UnsupportedOperationException();	
	}
	
	@Override
	public void setPosition(Coordinate coordinate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetPosition(Coordinate coordinate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStrand(STRAND strand) {
		throw new UnsupportedOperationException();
	}

	@Override
	public STRAND getStrand() {
		return coordinate.getStrand();
	}

	@Override
	public boolean isReverseStrand() {
		// FIXME Qi 
		// return false;
		return coordinate.isReverseStrand();
	}
	
	@Override
	public int getLength() {
		return coordinate.getLength();
	}

}
