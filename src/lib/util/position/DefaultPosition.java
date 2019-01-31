package lib.util.position;

import lib.recordextended.SAMRecordExtended;

public class DefaultPosition extends AbstractPosition {
	
	public DefaultPosition(
			final int refPos, final int readPos, final int winPos,
			final SAMRecordExtended recordExtended) {
		super(refPos, readPos, winPos, recordExtended);
	}
	
	public DefaultPosition(final Position pos) {
		super(
				pos.getReferencePosition(),
				pos.getReadPosition(),
				pos.getWindowPosition(),
				pos.getRecordExtended());
	}
	
	public DefaultPosition(final DefaultPosition pos) {
		super(pos);
	}
	
	@Override
	public DefaultPosition copy() {
		return new DefaultPosition(this);
	}
	
	@Override
	public boolean isValidReferencePosition() {
		return getReferencePosition() > 0;
	}
	
}
