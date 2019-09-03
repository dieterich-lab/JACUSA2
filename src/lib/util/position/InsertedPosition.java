package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.CigarElementExtended;
import lib.util.coordinate.CoordinateTranslator;

// TODO
public class InsertedPosition extends AbstractPosition {

	public InsertedPosition(final InsertedPosition insertedPosition) {
		super(insertedPosition);
	}
	
	public InsertedPosition(
			final AlignedPosition alignPos, 
			final Record record,
			final CoordinateTranslator translator) {

		super(
				alignPos.getRefPos(), 
				alignPos.getReadPos(), 
				translator.ref2winPos(alignPos.getRefPos()), 
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
					record.getCigarElementExtended().get(insertionIndex),
					record, 
					translator);
		}
		
		private Builder(
				final CigarElementExtended cigarElementExtended, final Record record, 
				final CoordinateTranslator translator) {
			
			super(
					cigarElementExtended.getPosition().getRefPos(), 
					cigarElementExtended.getPosition().getReadPos(),
					translator.ref2winPos(cigarElementExtended.getPosition().getRefPos()),
					record);
		}
		
		@Override
		public InsertedPosition build() {
			return new InsertedPosition(this);
		}
		
	}
	
}
