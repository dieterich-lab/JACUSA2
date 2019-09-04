package lib.util.position;

class IntervalPosProvider implements PositionProvider {
	
	private final AbstractPosition pos;
	private final int length;
	
	private int current;
	
	IntervalPosProvider(final AbstractPosition pos, final int length) {
		this.pos 	= pos;
		this.length = length;
		
		current		= 0;
	}
	
	@Override
	public boolean hasNext() {
		return current < length;
	}
	
	@Override
	public Position next() {
		Position tmpPos = new UnmodifiablePosition(pos);
		++current;
		pos.increment();
		return tmpPos;
	}
	
}
