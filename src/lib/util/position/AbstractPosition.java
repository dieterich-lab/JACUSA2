package lib.util.position;

import lib.recordextended.SAMRecordExtended;

// TODO Qi all PositionProviders need testing
abstract class AbstractPosition implements Position {
	
	private int refPos;
	private int readPos;
	private int winPos;
	
	private final SAMRecordExtended recordExtended;
	
	protected AbstractPosition(AbstractBuilder<? extends AbstractPosition> builder) {
		this.refPos		= builder.refPos;
		this.readPos	= builder.readPos;
		this.winPos		= builder.winPos;
		
		this.recordExtended 	= builder.recordExtended;
	}
	
	protected AbstractPosition(final AbstractPosition pos) {
		this.refPos			= pos.refPos;
		this.readPos		= pos.readPos;
		this.winPos			= pos.winPos;
		
		this.recordExtended = pos.recordExtended;
	}
	
	protected AbstractPosition(
			final int refPos, final int readPos, final int winPos, 
			final SAMRecordExtended recordExtended) {
		this.refPos			= refPos;
		this.readPos		= readPos;
		this.winPos			= winPos;
		
		this.recordExtended 	= recordExtended;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Position)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final Position pos = (Position)obj;
		return 
				refPos == pos.getReferencePosition() && 
				readPos == pos.getReadPosition() &&
				winPos == pos.getWindowPosition() &&
				recordExtended.getSAMRecord().equals(pos.getRecord());
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + refPos;
		hash = 31 * hash + readPos;
		hash = 31 * hash + winPos;
		hash = 31 * hash + getRecord().hashCode();
		return hash;
	}
	
	@Override
	public int getReferencePosition() {
		return refPos;
	}
	
	@Override
	public int getReadPosition() {
		return readPos;
	}
	
	@Override
	public int getWindowPosition() {
		return winPos;
	}
	
	@Override
	public SAMRecordExtended getRecordExtended() {
		return recordExtended;
	}

	void update(final int refPos, final int readPos, final int winPos) {
		this.refPos 	= refPos;
		this.readPos 	= readPos;
		this.winPos 	= winPos;
	}

	void increment() {
		++refPos;
		++readPos;
		++winPos;
	}
	
	void offset(final int offset) {
		refPos 	+= offset;
		readPos	+= offset;
		winPos	+= offset;
	}

	void setWindowPosition(final int winPos) {
		this.winPos = winPos;
	}
	
	void resetWindowPosition() {
		this.winPos = -1;
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d,%d)", refPos, readPos, winPos);
		/*
		return new StringBuilder()
				.append("1-based-ref=").append(getReferencePosition()).append(' ')
				.append("0-based-read=").append(getReadPosition()).append(' ')
				.append("0-based-win=").append(getWindowPosition()).append(' ')
				.append('\n')
				.append("base=").append(getReadBaseCall()).append(' ')
				.append("qual=").append(getReadBaseCallQuality()).append(' ')
				.toString();
				*/
	}

	static abstract class AbstractBuilder<T extends AbstractPosition> implements lib.util.Builder<T> {
		
		private int refPos;
		private int readPos;
		private int winPos;
		
		private final SAMRecordExtended recordExtended;
		
		protected AbstractBuilder(
				final int refPos, final int readPos, final int winPos,
				final SAMRecordExtended recordExtended) {
			
			this.refPos			= refPos;
			this.readPos 		= readPos;
			this.winPos			= winPos;
			
			this.recordExtended	= recordExtended;
		}
		
		protected void update(final int refPos, final int readPos, final int winPos) {
			this.refPos 	= refPos;
			this.readPos 	= readPos;
			this.winPos 	= winPos;
		}
	}
	
}
