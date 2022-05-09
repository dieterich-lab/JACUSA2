package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.ProcessedRecord;
import lib.record.ProcessedRecord.CigarDetail;
import lib.util.coordinate.CoordinateTranslator;

// TODO
class InsertedPosition extends AbstractPosition {

	InsertedPosition(final InsertedPosition insertedPosition) {
		super(insertedPosition);
	}
	
	InsertedPosition(
			final AlignedPosition alignPos, 
			final ProcessedRecord record,
			final CoordinateTranslator translator) {

		super(
				alignPos.getRefPos(), 
				alignPos.getReadPos(), 
				translator.ref2winPos(alignPos.getRefPos()), 
				record);
	}
	
	InsertedPosition(
			final int refPos, final int readPos, final int winPos, 
			final ProcessedRecord record) {

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
		refPos	+= offset;
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
				final int insertionIndex, final ProcessedRecord record, 
				final CoordinateTranslator translator) {
			
			this(
					record.getCigarDetail().get(insertionIndex),
					record, 
					translator);
		}
		
		private Builder(
				final CigarDetail cigarDetail, final ProcessedRecord record, 
				final CoordinateTranslator translator) {
			
			super(
					cigarDetail.getPosition().getRefPos(), 
					cigarDetail.getPosition().getReadPos(),
					translator.ref2winPos(cigarDetail.getPosition().getRefPos()),
					record);
		}
		
		@Override
		public InsertedPosition build() {
			return new InsertedPosition(this);
		}
		
	}
	
}
