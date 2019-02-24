package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;
import lib.recordextended.SAMRecordExtended.CigarElementExtended;

public class DeletedPosition extends AbstractPosition {

	public DeletedPosition(final DeletedPosition deletedPosition) {
		super(deletedPosition);
	}
	
	public DeletedPosition(
			final AlignedPosition alignPos, 
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {

		super(
				alignPos.getReferencePosition(), 
				alignPos.getReadPosition(), 
				translator.reference2windowPosition(alignPos.getReferencePosition()), 
				recordExtended);
	}
	
	DeletedPosition(
			final int refPos, final int readPos, final int winPos, 
			final SAMRecordExtended recordExtended) {

		super(refPos, readPos, winPos, recordExtended);
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
	public boolean isValidReferencePosition() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<DeletedPosition> {
		
		public Builder(
				final int deletionIndex, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			this(
					recordExtended.getCigarElementExtended().get(deletionIndex),
					recordExtended, 
					translator);
		}
		
		private Builder(
				final CigarElementExtended cigarElementExtended, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			super(
					cigarElementExtended.getPosition().getReferencePosition(), 
					cigarElementExtended.getPosition().getReadPosition(),
					translator.reference2windowPosition(cigarElementExtended.getPosition().getReferencePosition()),
					recordExtended);
		}
		
		@Override
		public DeletedPosition build() {
			return new DeletedPosition(this);
		}
		
	}
	
}
