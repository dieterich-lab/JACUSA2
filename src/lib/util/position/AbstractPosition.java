package lib.util.position;

import lib.record.ProcessedRecord;

/**
 * TODO
 */
abstract class AbstractPosition implements Position {
	
	protected int refPos;
	protected int readPos;
	protected int winPos;
	
	private final ProcessedRecord record;
	
	protected AbstractPosition(AbstractBuilder<? extends AbstractPosition> builder) {
		this.refPos		= builder.refPos;
		this.readPos	= builder.readPos;
		this.winPos		= builder.winPos;
		
		this.record 	= builder.record;
	}
	
	protected AbstractPosition(final AbstractPosition pos) {
		this.refPos			= pos.refPos;
		this.readPos		= pos.readPos;
		this.winPos			= pos.winPos;
		
		this.record = pos.record;
	}
	
	protected AbstractPosition(
			final int refPos, final int readPos, final int winPos, 
			final ProcessedRecord record) {
		this.refPos			= refPos;
		this.readPos		= readPos;
		this.winPos			= winPos;
		
		this.record 	= record;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Position)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final Position pos = (Position)obj;
		
		if (record != null && pos.getProcessedRecord() != null && 
				! record.getSAMRecord().equals(pos.getProcessedRecord().getSAMRecord())) {
			
			return false;
		}
		
		return 
				refPos == pos.getReferencePosition() && 
				readPos == pos.getReadPosition() &&
				winPos == pos.getWindowPosition();
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + refPos;
		hash = 31 * hash + readPos;
		hash = 31 * hash + winPos;
		if (getProcessedRecord() != null) {
			hash = 31 * hash + getSAMRecord().hashCode();
		}
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
	public ProcessedRecord getProcessedRecord() {
		return record;
	}

	void update(final int refPos, final int readPos, final int winPos) {
		this.refPos 	= refPos;
		this.readPos 	= readPos;
		this.winPos 	= winPos;
	}

	abstract void increment();
	
	abstract void offset(final int offset);

	void setWindowPosition(final int winPos) {
		this.winPos = winPos;
	}
	
	void resetWindowPosition() {
		this.winPos = -1;
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d,%d)", refPos, readPos, winPos);
	}
	
	abstract static class AbstractBuilder<T extends AbstractPosition> implements lib.util.Builder<T> {
		
		private int refPos;
		private int readPos;
		private int winPos;
		
		private final ProcessedRecord record;
		
		protected AbstractBuilder(
				final int refPos, final int readPos, final int winPos,
				final ProcessedRecord record) {
			
			this.refPos			= refPos;
			this.readPos 		= readPos;
			this.winPos			= winPos;
			
			this.record	= record;
		}
		
		protected void update(final int refPos, final int readPos, final int winPos) {
			this.refPos 	= refPos;
			this.readPos 	= readPos;
			this.winPos 	= winPos;
		}
	}
	
}
