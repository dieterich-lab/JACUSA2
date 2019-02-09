package lib.util.position;

import htsjdk.samtools.AlignmentBlock;
import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;

public class MatchPosition extends AbstractPosition {

	public MatchPosition(final MatchPosition matchPosition) {
		super(matchPosition);
	}
	
	public MatchPosition(
			final AlignedPosition alignPos, 
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		super(
				alignPos.getReferencePosition(), 
				alignPos.getReadPosition(), 
				translator.reference2windowPosition(alignPos.getReferencePosition()), 
				recordExtended);
	}
	
	MatchPosition(
			final int refPos, final int readPos, final int winPos, 
			final SAMRecordExtended recordExtended) {

		super(refPos, readPos, winPos, recordExtended);
	}
	
	private MatchPosition(Builder builder) {
		super(builder);
	}

	@Override
	public MatchPosition copy() {
		return new MatchPosition(this);
	}
	
	@Override
	public boolean isValidReferencePosition() {
		return true;
	}
	
	public static class Builder extends AbstractBuilder<MatchPosition> {
		
		public Builder(
				final int alignmentBlockIndex, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			this(
					recordExtended.getSAMRecord().getAlignmentBlocks().get(alignmentBlockIndex),
					recordExtended, 
					translator);
		}
		
		private Builder(
				final AlignmentBlock alignmentBlock, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			super(
					alignmentBlock.getReferenceStart(), 
					alignmentBlock.getReadStart() - 1,
					translator.reference2windowPosition(alignmentBlock.getReferenceStart()),
					recordExtended);
		}
		
		@Override
		public MatchPosition build() {
			return new MatchPosition(this);
		}
		
	}
	
}
