package lib.util.position;

import htsjdk.samtools.AlignmentBlock;
import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.SAMRecordExtended;

public class MatchPosition extends AbstractPosition {

	private int matches;

	public MatchPosition(final MatchPosition matchPosition) {
		super(matchPosition);
		this.matches = matchPosition.matches;
	}
	
	MatchPosition(
			final int refPos, final int readPos, final int winPos, 
			final SAMRecordExtended recordExtended) {

		super(refPos, readPos, winPos, recordExtended);
	}
	
	private MatchPosition(Builder builder) {
		super(builder);
		this.matches = builder.matches;
	}

	@Override
	public MatchPosition copy() {
		return new MatchPosition(this);
	}
	
	@Override
	public boolean isValidReferencePosition() {
		return true;
	}

	@Override
	void increment() {
		super.increment();
		++matches;
	}
	
	@Override
	void offset(int offset) {
		super.offset(offset);
		matches += offset;
	}
	
	void update(final int refPos, final int readPos, final int winPos, final int matches) {
		update(refPos, readPos, winPos);
		this.matches = matches;
	}
	
	int getMatches() {
		return matches;
	}
	
	public static class Builder extends AbstractBuilder<MatchPosition> {

		private int matches;
		
		public Builder(
				final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			this(
					recordExtended.getSAMRecord().getAlignmentBlocks().size() > 0 ? 1 : -1,
					recordExtended, 
					translator);
		}
		
		public Builder(
				final int alignmentBlockIndex, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			this(
					getMatches(alignmentBlockIndex, recordExtended),
					recordExtended.getSAMRecord().getAlignmentBlocks().get(alignmentBlockIndex),
					recordExtended, 
					translator);
		}
		
		private Builder(
				final int matches,
				final AlignmentBlock alignmentBlock, final SAMRecordExtended recordExtended, 
				final CoordinateTranslator translator) {
			
			super(
					alignmentBlock.getReferenceStart(), 
					alignmentBlock.getReadStart(),
					translator.reference2windowPosition(alignmentBlock.getReferenceStart()),
					recordExtended);
			
			this.matches = matches;
		}
		
		@Override
		public MatchPosition build() {
			return new MatchPosition(this);
		}
		
		private static int getMatches(
				final int alignmentBlockIndex, final SAMRecordExtended recordExtended) {
			
			int matches = 0;
			for (int i = 0; i < alignmentBlockIndex; ++i) {
				matches += recordExtended.getSAMRecord().getAlignmentBlocks().get(i).getLength();
			}
			return matches;
		}
		
	}
	
}
