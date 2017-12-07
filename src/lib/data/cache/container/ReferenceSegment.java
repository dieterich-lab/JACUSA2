package lib.data.cache.container;

public class ReferenceSegment {

	enum TYPE {UNKNOWN, COVERED, NOT_COVERED}
	
	private int id;

	private TYPE type;
	
	private int start;
	private int end;
	
	public ReferenceSegment(final ReferenceSegmentContainer segmentContainer, final TYPE type, 
			final int start, final int end) {

		id = segmentContainer.getNextId();

		this.type = type;
		
		this.start = start;
		this.end = end;
	}
	public void updateStart(final int start) {
		this.start = start;
	}

	public void updateEnd(final int end) {
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
		return sb.toString();
	}

}