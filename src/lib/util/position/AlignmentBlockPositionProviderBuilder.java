package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import lib.recordextended.SAMRecordExtended;

public class AlignmentBlockPositionProviderBuilder implements lib.util.Builder<IntervalPositionProvider> {
	
	private final MatchPosition pos;
	private int length;

	private CoordinateTranslator translator;

	public AlignmentBlockPositionProviderBuilder(
			final int alignmentBlockIndex, final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		pos 	= new MatchPosition.Builder(alignmentBlockIndex, recordExtended, translator)
				.build();
		length 	= recordExtended.getSAMRecord()
				.getAlignmentBlocks().get(alignmentBlockIndex).getLength();
		
		this.translator = translator;
	}

	// call this once
	public AlignmentBlockPositionProviderBuilder tryFirst(final int length) {
		this.length = Math.min(this.length, length);
		return this;
	}
	
	// call this once
	public AlignmentBlockPositionProviderBuilder tryLast(final int length) {
		int oldLength = this.length;
		this.length = Math.min(this.length, length);
		final int offset = oldLength - this.length;
		pos.offset(offset);
		return this;
	}
	
	// call this once
	public AlignmentBlockPositionProviderBuilder ignoreFirst(final int length) {
		if (length <= this.length) {
			this.length = this.length -length;
			pos.offset(length);
		} else {
			pos.offset(this.length);
			this.length = 0;
		}
		return this;
	}

	// call this once
	public AlignmentBlockPositionProviderBuilder ignoreLast(final int length) {
		final int offset = this.length - length;
		this.length = Math.max(0, offset);
		return this;
	}
	
	// make sure to run this last
	public AlignmentBlockPositionProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPositionProvider build() {
		return new IntervalPositionProvider(pos, length); 
	}
	
}
