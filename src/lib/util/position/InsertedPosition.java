package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;
import lib.recordextended.SAMRecordExtended.CigarElementExtended;

public class InsertedPosition extends AbstractPosition {

	public InsertedPosition(final InsertedPosition insertedPosition) {
		super(insertedPosition);
	}
	
	public InsertedPosition(
			final AlignedPosition alignPos, 
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {

		super(
				alignPos.getReferencePosition(), 
				alignPos.getReadPosition(), 
				translator.reference2windowPosition(alignPos.getReferencePosition()), 
				recordExtended);
	}
	
	InsertedPosition(
			final int refPos, final int readPos, final int winPos, 
			final SAMRecordExtended recordExtended) {

		super(refPos, readPos, winPos, recordExtended);
	}
	
	private InsertedPosition(Builder builder) {
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
	public InsertedPosition copy() {
		return new InsertedPosition(this);
	}
	
	@Override
	public boolean isValidReferencePosition() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<InsertedPosition> {
		
		public Builder(
				final int insertionIndex, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			this(
					recordExtended.getCigarElementExtended().get(insertionIndex),
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
		public InsertedPosition build() {
			return new InsertedPosition(this);
		}
		
	}
	
}
