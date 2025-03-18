package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.AlignedPositionCigarElement;
import lib.util.coordinate.CoordinateTranslator;

class InsertedPosition extends AbstractPosition {

	InsertedPosition(final InsertedPosition insertedPosition) {
		super(insertedPosition);
	}
	
	InsertedPosition(
			final AlignedPosition alignPos, 
			final Record record,
			final CoordinateTranslator translator) {

		super(
				alignPos.getRefPosition(), 
				alignPos.getReadPosition(), 
				translator.ref2winPos(alignPos.getRefPosition()), 
				record);
	}
	
	InsertedPosition(
			final int refPos, final int readPos, final int winPos, 
			final Record record) {

		super(refPos, readPos, winPos, record);
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
	public boolean isValidRefPos() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<InsertedPosition> {
		
		public Builder(
				final int insertionIndex, final Record record, 
				final CoordinateTranslator translator) {
			
			this(
					record.getCigarDetail().get(insertionIndex),
					record, 
					translator);
		}
		
		private Builder(
				final AlignedPositionCigarElement cigarDetail, final Record record, 
				final CoordinateTranslator translator) {
			
			super(
					cigarDetail.getPosition().getRefPosition(), 
					cigarDetail.getPosition().getReadPosition(),
					translator.ref2winPos(cigarDetail.getPosition().getRefPosition()),
					record);
		}
		
		@Override
		public InsertedPosition build() {
			return new InsertedPosition(this);
		}
		
	}
	
}
