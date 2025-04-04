package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.AlignedPositionCigarElement;
import lib.util.coordinate.CoordinateTranslator;

/**
 * DOCUMENT
 */
class DeletionPositionProviderBuilder implements lib.util.Builder<IntervalPosProvider> {
	
	// position of the deletion
	private final DeletedPosition pos;
	// length of deletion
	private int length;
	
	private CoordinateTranslator translator;
	
	DeletionPositionProviderBuilder(
			final int delI, final Record record, 
			final CoordinateTranslator translator) {
		
		// extract corresponding cigar element 
		final int cigarElementI 	= record.getDeletion().get(delI);
		final AlignedPositionCigarElement cigarElement = record
				.getCigarDetail().get(cigarElementI);
		
		// prepare to create Position
		final AlignedPosition alignedPos 	= cigarElement.getPosition(); 
		final int refPos 					= alignedPos.getRefPosition();
		final int readPos 					= alignedPos.getReadPosition();
		final int winPos					= translator.ref2winPos(refPos);
		
		pos 	= new DeletedPosition(refPos, readPos, winPos, record);
		length 	= cigarElement.getCigarElement().getLength();
		
		this.translator = translator;
	}

	// make sure to run this last
	public DeletionPositionProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPosProvider build() {
		return new IntervalPosProvider(pos, length); 
	}
	
}
