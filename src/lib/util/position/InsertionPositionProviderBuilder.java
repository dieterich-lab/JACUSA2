package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;
import lib.recordextended.SAMRecordExtended.CigarElementExtended;

public class InsertionPositionProviderBuilder implements lib.util.Builder<IntervalPositionProvider> {
	
	// position of the insertion
	private final InsertedPosition pos;
	// length of insertion
	private int length;

	private CoordinateTranslator translator;

	public InsertionPositionProviderBuilder(
			final int insertionIndex, final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		// extract corresponding cigar element 
		final int cigarElementExtendedIndex 	= recordExtended.getInsertion().get(insertionIndex);
		final CigarElementExtended cigarElement = recordExtended
				.getCigarElementExtended().get(cigarElementExtendedIndex);
		
		// prepare to create Position
		final AlignedPosition alignedPos 	= cigarElement.getPosition(); 
		final int refPos 					= alignedPos.getReferencePosition();
		final int readPos 					= alignedPos.getReadPosition();
		final int winPos					= translator.reference2windowPosition(refPos);
		
		pos 	= new InsertedPosition(refPos, readPos, winPos, recordExtended);
		length 	= cigarElement.getCigarElement().getLength();
		
		this.translator = translator;
	}

	// make sure to run this last
	public InsertionPositionProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPositionProvider build() {
		return new IntervalPositionProvider(pos, length); 
	}
	
}
