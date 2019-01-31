package lib.util.position;

public class AlignedPositionProvider implements PositionProvider {

	private final MatchPosition pos;
	private final int length;
	
	private int current;
	
	AlignedPositionProvider(final MatchPosition pos, final int length) {
		this.pos 	= pos;
		this.length = length;
		
		current		= 0;
	}
	
	@Override
	public boolean hasNext() {
		return pos != null && current < length;
	}
	
	@Override
	public Position next() {
		Position tmpPos = new DefaultPosition(pos);
		++current;
		pos.increment();
		return tmpPos;
	}
	
}
