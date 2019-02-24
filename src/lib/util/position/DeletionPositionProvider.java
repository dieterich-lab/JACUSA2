package lib.util.position;

import java.util.Iterator;

import lib.recordextended.SAMRecordExtended;

public class DeletionPositionProvider implements PositionProvider {
	
	private final SAMRecordExtended recordExtended;
	
	private final Iterator<Integer> it;
	private Position pos;
	
	public DeletionPositionProvider(final SAMRecordExtended recordExtended) {
		this.recordExtended = recordExtended;
		it = recordExtended.getDeletion().iterator();
	}
	
	@Override
	public boolean hasNext() {
		if (pos != null) {
			return true;
		}
		while (it.hasNext()) {
			// TODO
		}
		return false;
	}
	
	@Override
	public Position next() {
		final Position tmpPos = new DefaultPosition(pos);
		pos 			= null;
		return tmpPos;
	}
	
}
