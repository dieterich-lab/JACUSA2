package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.CigarElementExtended;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
 */
public class DeletionPosProviderBuilder implements lib.util.Builder<IntervalPosProvider> {
	
	// position of the deletion
	private final DeletedPosition pos;
	// length of deletion
	private int length;

	private CoordinateTranslator translator;

	public DeletionPosProviderBuilder(
			final int delI, final Record record, 
			final CoordinateTranslator translator) {
		
		// extract corresponding cigar element 
		final int cigarElementI 	= record.getDeletion().get(delI);
		final CigarElementExtended cigarElement = record
				.getCigarElementExtended().get(cigarElementI);
		
		// prepare to create Position
		final AlignedPosition alignedPos 	= cigarElement.getPosition(); 
		final int refPos 					= alignedPos.getRefPos();
		final int readPos 					= alignedPos.getReadPos();
		final int winPos					= translator.ref2winPos(refPos);
		
		pos 	= new DeletedPosition(refPos, readPos, winPos, record);
		length 	= cigarElement.getCigarElement().getLength();
		
		this.translator = translator;
	}

	// make sure to run this last
	public DeletionPosProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPosProvider build() {
		return new IntervalPosProvider(pos, length); 
	}
	
}
