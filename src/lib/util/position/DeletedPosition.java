package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.CigarDetail;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
 */
class DeletedPosition extends AbstractPosition {

	DeletedPosition(final DeletedPosition deletedPosition) {
		super(deletedPosition);
	}
	
	DeletedPosition(
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
					record.getCigarDetail().get(deletionIndex),
					record, 
					translator);
		}
		
		private Builder(
				final CigarDetail cigarElement, final Record record, 
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
