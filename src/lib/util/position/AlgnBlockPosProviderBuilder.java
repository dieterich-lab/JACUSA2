package lib.util.position;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
 */
public class AlgnBlockPosProviderBuilder implements lib.util.Builder<IntervalPosProvider> {
	
	private final MatchPosition pos;
	private int length;

	private CoordinateTranslator translator;

	public AlgnBlockPosProviderBuilder(
			final int algnBlockIndex, final Record record, 
			final CoordinateTranslator translator) {
		
		pos 	= new MatchPosition.Builder(algnBlockIndex, record, translator)
				.build();
		length 	= record.getSAMRecord()
				.getAlignmentBlocks().get(algnBlockIndex).getLength();
		
		this.translator = translator;
	}

	// call this once
	public AlgnBlockPosProviderBuilder tryFirst(final int length) {
		this.length = Math.min(this.length, length);
		return this;
	}
	
	// call this once
	public AlgnBlockPosProviderBuilder tryLast(final int length) {
		int oldLength = this.length;
		this.length = Math.min(this.length, length);
		final int offset = oldLength - this.length;
		pos.offset(offset);
		return this;
	}
	
	// call this once
	public AlgnBlockPosProviderBuilder ignoreFirst(final int length) {
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
	public AlgnBlockPosProviderBuilder ignoreLast(final int length) {
		final int offset = this.length - length;
		this.length = Math.max(0, offset);
		return this;
	}
	
	// make sure to run this last
	public AlgnBlockPosProviderBuilder adjustWindowPos() {
		length = PositionProvider.adjustWindowPos(pos, length, translator);
		return this;
	} 
	
	@Override
	public IntervalPosProvider build() {
		return new IntervalPosProvider(pos, length); 
	}
	
}
