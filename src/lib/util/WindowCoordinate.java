package lib.util;

@Deprecated
public class WindowCoordinate extends Coordinate {

	private int windowSize;
	private int maxGenomicPosition;

	public WindowCoordinate(final String contig, 
			final int genomicWindowStart, 
			final int windowSize, 
			final int maxGenomicPosition) {
		setContig(contig);
		setStart(genomicWindowStart);
		this.windowSize = windowSize;
		setMaxReferencePosition(maxGenomicPosition);
	}

	public WindowCoordinate(final Coordinate coordinate, final int windowSize, final int maxReferencePosition) {
		setContig(coordinate.getContig());
		setStart(coordinate.getStart());
		this.windowSize = windowSize;
		setMaxReferencePosition(maxReferencePosition);
	}
	
	public void setStart(final int start) {
		super.setStart(start);
		_setEnd();
	}
	
	/**
	 * End of window (inclusive)
	 * @return
	 */
	private void _setEnd() {
		setEnd(Math.min(getStart() + windowSize - 1, maxGenomicPosition));
	}
	
	public int getWindowSize() {
		return windowSize;
	}

	public int getMaxGenomicPosition() {
		return maxGenomicPosition;
	}

	public void setMaxReferencePosition(int maxGenomicPosition) {
		this.maxGenomicPosition = maxGenomicPosition;
		_setEnd();
	}

	/**
	 * 
	 * @param referencePosition
	 * @return
	 */
	public boolean isContainedInWindow(int referencePosition) {
		return referencePosition >= getStart() && referencePosition <= getEnd();
	}

	// TODO start + end and not only position
	public boolean isContainedInWindow(final Coordinate coordinate) {
		return coordinate.getPosition() >= getStart() && coordinate.getPosition() <= getEnd();
	}
	
	/**
	 * Calculates genomicPosition or -1 or -2 if genomicPosition is outside the window
	 * -1 if downstream of windowEnd
	 * -2 if upstream of windowStart
	 * @param referencePosition
	 * @return
	 */
	public int convert2WindowPosition(final int referencePosition) {
		/*
		if(genomicPosition < genomicWindowStart) {
			return -2;
		} else if(genomicPosition > getGenomicWindowEnd()){
			return -1;
		}
		*/
		
		if(referencePosition > getEnd()){
			return -1;
		}

		return referencePosition - getStart();
	}

	/**
	 * 
	 * @param windowPosition
	 * @return
	 */
	public int getGenomicPosition(int windowPosition) {
		return getStart() + windowPosition;
	}
	
	public int getOrientation(final int genomicPosition) {
		if(genomicPosition < getStart()) {
			return -1;
		}
		
		if(genomicPosition > getEnd()){
			return 1;
		}
		
		return 0;
	}
	
}