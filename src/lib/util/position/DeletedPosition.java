package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.CigarElementExtended;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
 */
public class DeletedPosition extends AbstractPosition {

	public DeletedPosition(final DeletedPosition deletedPosition) {
		super(deletedPosition);
	}
	
	public DeletedPosition(
			final AlignedPosition alignPos, 
			final Record record,
			final CoordinateTranslator translator) {

		super(
				alignPos.getRefPos(), 
				alignPos.getReadPos(), 
				translator.ref2winPos(alignPos.getRefPos()), 
				record);
	}
	
	DeletedPosition(
			final int refPos, final int readPos, final int winPos, 
			final Record record) {

		super(refPos, readPos, winPos, record);
	}
	
	private DeletedPosition(Builder builder) {
		super(builder);
	}

	@Override
	void increment() {
		refPos++;
		winPos++;
	}
	
	@Override
	void offset(int offset) {
		refPos 	+= offset;
		winPos	+= offset;
	}
	
	@Override
	public DeletedPosition copy() {
		return new DeletedPosition(this);
	}
	
	@Override
	public boolean isValidRefPos() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<DeletedPosition> {
		
		public Builder(
				final int deletionIndex, final Record record, 
				final CoordinateTranslator translator) {
			
			this(
					record.getCigarElementExtended().get(deletionIndex),
					record, 
					translator);
		}
		
		private Builder(
				final CigarElementExtended cigarElement, final Record record, 
				final CoordinateTranslator translator) {
			
			super(
					cigarElement.getPosition().getRefPos(), 
					cigarElement.getPosition().getReadPos(),
					translator.ref2winPos(cigarElement.getPosition().getRefPos()),
					record);
		}
		
		@Override
		public DeletedPosition build() {
			return new DeletedPosition(this);
		}
		
	}
	
}
