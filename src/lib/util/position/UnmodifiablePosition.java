package lib.util.position;

import lib.record.Record;

/**
 * Wrapper for an instance of Position that modification of positional data.
 */
public class UnmodifiablePosition extends AbstractPosition {
	
	public UnmodifiablePosition(
			final int refPos, final int readPos, final int winPos,
			final Record record) {
		super(refPos, readPos, winPos, record);
	}
	
	public UnmodifiablePosition(final Position pos) {
		super(
				pos.getReferencePosition(),
				pos.getReadPosition(),
				pos.getWindowPosition(),
				pos.getRecord());
	}
	
	@Override
	void increment() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	void offset(int offset) {
		throw new UnsupportedOperationException();
	}
	
	public UnmodifiablePosition(final UnmodifiablePosition pos) {
		super(pos);
	}
	
	@Override
	public UnmodifiablePosition copy() {
		return new UnmodifiablePosition(this);
	}
	
	@Override
	public boolean isValidRefPos() {
		return getReferencePosition() > 0;
	}
	
}
