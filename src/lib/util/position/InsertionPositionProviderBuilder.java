package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.AlignedPositionCigarElement;
import lib.util.coordinate.CoordinateTranslator;

// FIXME count only the start position
class InsertionPositionProviderBuilder implements lib.util.Builder<IntervalPosProvider> {
	
	// position of the insertion
	private final InsertedPosition pos;
	// length of insertion
	private int length;

	private CoordinateTranslator translator;

	InsertionPositionProviderBuilder(
			final int insI, final Record record, 
			final CoordinateTranslator translator) {
		this(insI, record, translator, false);
	}
	
	InsertionPositionProviderBuilder(
			final int insI, final Record record, 
			final CoordinateTranslator translator,
			final boolean onlyStart) {
		
		// extract corresponding cigar element 
		final int cigarElementI 	= record.getInsertion().get(insI);
		final AlignedPositionCigarElement cigarElement = record
				.getCigarDetail().get(cigarElementI);
		
		// prepare to create Position
		final AlignedPosition alignedPos 	= cigarElement.getPosition(); 
		final int refPos 					= alignedPos.getRefPosition();
		final int readPos 					= alignedPos.getReadPosition();
		final int winPos					= translator.ref2winPos(refPos);
		
		pos 	= new InsertedPosition(refPos, readPos, winPos, record);
		
		length 	= cigarElement.getCigarElement().getLength();
		if (onlyStart) {
			length = 1;
		}
		
		this.translator = translator;
	}

	// make sure to run this last
	public InsertionPositionProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPosProvider build() {
		return new IntervalPosProvider(pos, length); 
	}
	
}
