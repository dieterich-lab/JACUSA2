package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.SAMRecordExtended;

public class AlignmentBlockBuilder implements lib.util.Builder<AlignedPositionProvider> {
	
	private final MatchPosition pos;
	private int length;

	private CoordinateTranslator translator;

	public AlignmentBlockBuilder(
			final int alignmentBlockIndex, final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		pos 	= new MatchPosition.Builder(alignmentBlockIndex, recordExtended, translator)
				.build();
		length 	= recordExtended.getSAMRecord()
				.getAlignmentBlocks().get(alignmentBlockIndex).getLength();
		
		this.translator = translator;
	}

	// call this once
	public AlignmentBlockBuilder tryFirst(final int length) {
		this.length = Math.min(this.length, length);
		return this;
	}
	
	// call this once
	public AlignmentBlockBuilder tryLast(final int length) {
		int offset = this.length - 1;
		this.length = Math.min(this.length, length);
		offset -= (this.length - 1);
		pos.offset(offset);
		return this;
	}
	
	// call this once
	public AlignmentBlockBuilder ignoreFirst(final int length) {
		final int offset = this.length - length;
		if (offset < 0) {
			pos.offset(this.length - 1);
			this.length = 0;
		} else {
			pos.offset(offset);
			this.length = offset;
		}
		return this;
	}

	// call this once
	public AlignmentBlockBuilder ignoreLast(final int length) {
		final int offset = this.length - length;
		this.length = Math.max(0, offset);
		return this;
	}
	
	// make sure to run this last
	public AlignmentBlockBuilder adjustForWindow() {
		length = PositionProvider.adjustForWindow(pos, length, translator);
		return this;
	} 
	
	@Override
	public AlignedPositionProvider build() {
		return new AlignedPositionProvider(pos, length); 
	}
	
}
