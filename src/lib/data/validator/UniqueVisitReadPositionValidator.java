package lib.data.validator;

import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.util.position.Position;

/**
 * DOCUMENT
 */
public class UniqueVisitReadPositionValidator 
implements Validator {

	private final VisitedReadPositionStorage storage;
	
	public UniqueVisitReadPositionValidator(final VisitedReadPositionStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public boolean isValid(Position pos) {
		return ! storage.isVisited(pos.getReadPosition());
	}
	
}
