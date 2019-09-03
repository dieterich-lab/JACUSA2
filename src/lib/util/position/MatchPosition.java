package lib.util.position;

import htsjdk.samtools.AlignmentBlock;
import lib.record.AlignedPosition;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

public class MatchPosition extends AbstractPosition {

	public MatchPosition(final MatchPosition matchPosition) {
		super(matchPosition);
	}
	
	public MatchPosition(
			final AlignedPosition alignPos, 
			final Record record,
			final CoordinateTranslator translator) {
		super(
				alignPos.getRefPos(), 
				alignPos.getReadPos(), 
				translator.ref2winPos(alignPos.getRefPos()), 
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
				final int alignmentBlockIndex, final Record record, 
				final CoordinateTranslator translator) {
			
			this(
					record.getSAMRecord().getAlignmentBlocks().get(alignmentBlockIndex),
					record, 
					translator);
		}
		
		private Builder(
				final AlignmentBlock alignmentBlock, final Record record, 
				final CoordinateTranslator translator) {
			
			super(
					alignmentBlock.getReferenceStart(), 
					alignmentBlock.getReadStart() - 1,
					translator.ref2winPos(alignmentBlock.getReferenceStart()),
					record);
		}
		
		@Override
		public MatchPosition build() {
			return new MatchPosition(this);
		}
		
	}
	
}
