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
	
	private final Iterator<AlignedPosition> misMatchPosIt;
	
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
		
		misMatchPosIt = mismatchPositions.iterator();
	}

	@Override
	public boolean hasNext() {
		while (nextPos == null && misMatchPosIt.hasNext()) {
			final AlignedPosition tmpMisMatchPos = misMatchPosIt.next();
			final int refPos 	= tmpMisMatchPos.getReferencePosition();
			final int readPos 	= tmpMisMatchPos.getReadPosition();
			final int winPos 	= translator.reference2windowPosition(refPos);
			final Position misMatchPos 	= new UnmodifiablePosition(refPos, readPos, winPos, recordExtended);
			final Base refBase 			= recordExtended
					.getRecordReferenceProvider()
					.getReferenceBase(misMatchPos.getReferencePosition(), misMatchPos.getReadPosition());
			if (refBase == Base.N) {
				continue;
			}
			if (validator.isValid(misMatchPos)) {
				nextPos = misMatchPos;
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
