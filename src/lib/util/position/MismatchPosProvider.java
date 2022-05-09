package lib.util.position;

import java.util.Iterator;
import java.util.List;

import lib.data.validator.CombinedValidator;
import lib.data.validator.Validator;
import lib.record.AlignedPosition;
import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.coordinate.CoordinateTranslator;

public class MismatchPosProvider implements PositionProvider {

	private final ProcessedRecord record;
	private final CoordinateTranslator translator;
	private final Validator validator;
	
	private final Iterator<AlignedPosition> misMatchPosIt;
	
	private Position nextPos;

	public MismatchPosProvider(
			final ProcessedRecord record,
			final CoordinateTranslator translator,
			final List<Validator> validators) {
		
		this(record, translator, new CombinedValidator(validators));
	}
	
	public MismatchPosProvider(
			final ProcessedRecord record,
			final CoordinateTranslator translator,
			final Validator validator) {
		
		this.record = record;
		this.translator		= translator;
		this.validator		= validator;
		List<AlignedPosition> mismatchPositions = record.getRecordReferenceProvider()
				.getMismatchPositions();
		
		misMatchPosIt = mismatchPositions.iterator();
	}

	@Override
	public boolean hasNext() {
		while (nextPos == null && misMatchPosIt.hasNext()) {
			final AlignedPosition tmpMisMatchPos = misMatchPosIt.next();
			final int refPos 	= tmpMisMatchPos.getRefPos();
			final int readPos 	= tmpMisMatchPos.getReadPos();
			final int winPos 	= translator.ref2winPos(refPos);
			final Position misMatchPos 	= new UnmodifiablePosition(refPos, readPos, winPos, record);
			final Base refBase 			= record
					.getRecordReferenceProvider()
					.getRefBase(misMatchPos.getReferencePosition(), misMatchPos.getReadPosition());
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
