package lib.util.position;

import htsjdk.samtools.AlignmentBlock;
import lib.record.AlignedPosition;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

public class MatchPosition extends AbstractPosition {

	public MatchPosition(final MatchPosition matchPos) {
		super(matchPos);
	}
	
	public MatchPosition(
			final AlignedPosition alignPos, 
			final Record record,
			final CoordinateTranslator translator) {
		super(
				alignPos.getRefPosition(), 
				alignPos.getReadPosition(), 
				translator.ref2winPos(alignPos.getRefPosition()), 
				record);
	}
	
	MatchPosition(
			final int refPos, final int readPos, final int winPos, 
			final Record record) {

		super(refPos, readPos, winPos, record);
	}
	
	@Override
	void increment() {
		refPos++;
		readPos++;
		winPos++;
	}
	
	@Override
	void offset(int offset) {
		refPos 	+= offset;
		readPos	+= offset;
		winPos	+= offset;
	}
	
	private MatchPosition(Builder builder) {
		super(builder);
	}
	
	@Override
	public MatchPosition copy() {
		return new MatchPosition(this);
	}
	
	@Override
	public boolean isValidRefPos() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<MatchPosition> {
		
		public Builder(
				final int algnBlockI, final Record record, 
				final CoordinateTranslator translator) {
			
			this(
					record.getSAMRecord().getAlignmentBlocks().get(algnBlockI),
					record, 
					translator);
		}
		
		private Builder(
				final AlignmentBlock algnBlock, final Record record, 
				final CoordinateTranslator translator) {
			
			super(
					algnBlock.getReferenceStart(), 
					algnBlock.getReadStart() - 1,
					translator.ref2winPos(algnBlock.getReferenceStart()),
					record);
		}
		
		@Override
		public MatchPosition build() {
			return new MatchPosition(this);
		}
		
	}
	
}
