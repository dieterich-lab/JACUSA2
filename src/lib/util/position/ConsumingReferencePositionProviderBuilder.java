package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;
import htsjdk.samtools.SAMRecord;
import lib.recordextended.SAMRecordExtended;

public class ConsumingReferencePositionProviderBuilder implements lib.util.Builder<IntervalPositionProvider> {
	
	private final AbstractPosition pos;
	private int length;

	public ConsumingReferencePositionProviderBuilder(
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		pos 	= new MatchPosition.Builder(0, recordExtended, translator)
				.build();
		
		final SAMRecord record 	= recordExtended.getSAMRecord();
		final int tmpLength  	= record.getAlignmentEnd() - record.getAlignmentStart() + 1;
		length = PositionProvider.adjustWindowPos(pos, tmpLength, translator);
	}
	
	@Override
	public IntervalPositionProvider build() {
		return new IntervalPositionProvider(pos, length); 
	}
	
}
