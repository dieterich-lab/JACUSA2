package lib.util.position;

import lib.recordextended.SAMRecordExtended;

public class UnmodifiablePosition extends AbstractPosition {
	
	public UnmodifiablePosition(
			final int refPos, final int readPos, final int winPos,
			final SAMRecordExtended recordExtended) {
		super(refPos, readPos, winPos, recordExtended);
	}
	
	public UnmodifiablePosition(final Position pos) {
		super(
				pos.getReferencePosition(),
				pos.getReadPosition(),
				pos.getWindowPosition(),
				pos.getRecordExtended());
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
	public boolean isValidReferencePosition() {
		return getReferencePosition() > 0;
	}
	
}
