package lib.data.cache.container;

import lib.data.cache.container.NextPositionSegmentContainer.TYPE;

public class NextPositionSegment {

	private int id;

	private TYPE type;
	private int next;
	
	private int start;
	private int end;
	
	public NextPositionSegment(final NextPositionSegmentContainer segmentContainer, final TYPE type, 
			final int start, final int end) {

		id = segmentContainer.getNextId();

		this.type = type;
		next = -1;
		
		this.start = start;
		this.end = end;
	}

	public NextPositionSegment(final NextPositionSegmentContainer segmentContainer, final TYPE type, 
			final int start, final int end, final int nextPosition) {

		this(segmentContainer, type, start, end);

		updateNext(nextPosition);
	}
	
	public void updateNext(final int next) {
		if (next >= 0 && next < end) {
			throw new IllegalStateException("t: " + type + " s: " + start + " e: " + end + " n: " + next);
		}
		this.next = next;
	}

	public void updateStart(final int start) {
		this.start = start;
	}

	public void updateEnd(final int end) {
		if (next >= 0 && next < end) {
			throw new IllegalStateException(
					"t: " + type + 
					" s: " + start + 
					" e: " + end + 
					" n: " + next);
		}
		this.end = end;
	}
	
	public int getId() {
		return id;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getNext() {
		return next;
	}

	public TYPE getType() {
		return type;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(' ');
		sb.append(type);
		sb.append(' ');
		sb.append(start);
		sb.append(':');
		sb.append(end);
		sb.append(' ');
		sb.append(next);
		return sb.toString();
	}

}