package lib.util.position;

import java.util.Iterator;
import java.util.List;

import lib.data.validator.CombinedValidator;
import lib.data.validator.Validator;
import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;
import lib.util.Base;
import lib.util.coordinate.CoordinateTranslator;

public class MismatchPositionProvider implements PositionProvider {

	private final SAMRecordExtended recordExtended;
	private final CoordinateTranslator translator;
	private final Validator validator;
	
	private final Iterator<AlignedPosition> combPosIt;
	
	private Position nextPos;

	public MismatchPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator,
			final List<Validator> validators) {
		
		this(recordExtended, translator, new CombinedValidator(validators));
	}
	
	public MismatchPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator,
			final Validator validator) {
		
		this.recordExtended = recordExtended;
		this.translator		= translator;
		this.validator		= validator;
		List<AlignedPosition> mismatchPositions = recordExtended.getRecordReferenceProvider()
				.getMismatchPositions();
		
		combPosIt 			= mismatchPositions.iterator();
	}

	@Override
	public boolean hasNext() {
		while (nextPos == null && combPosIt.hasNext()) {
			final AlignedPosition combPos = combPosIt.next();
			final int refPos 	= combPos.getReferencePosition();
			final Base refBase 	= recordExtended.getRecordReferenceProvider().getReferenceBase(refPos);
			if (refBase == Base.N) {
				continue;
			}
			final int readPos 	= combPos.getReadPosition();
			final int winPos	= translator.reference2windowPosition(refPos);
			final Position tmpNextPos = new UnmodifiablePosition(refPos, readPos, winPos, recordExtended);
			if (validator.isValid(tmpNextPos)) {
				nextPos = tmpNextPos;
				return true;
			}
		}
		return false;
	}

	@Override
	public Position next() {
		final Position tmpNextPos = nextPos.copy();
		nextPos = null;
		return tmpNextPos;
	}
	
}
