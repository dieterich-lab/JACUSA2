package lib.util.position;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import htsjdk.samtools.SAMRecord;

/**
 * TODO add documentation
 */
public class ConsumingRefPosProviderBuilder implements lib.util.Builder<IntervalPosProvider> {
	
	private final AbstractPosition pos;
	private int length;

	public ConsumingRefPosProviderBuilder(
			final Record record, 
			final CoordinateTranslator translator) {
		
		pos 	= new MatchPosition.Builder(0, record, translator)
				.build();
		
		final SAMRecord samRecord 	= record.getSAMRecord();
		final int tmpLength  		= samRecord.getAlignmentEnd() - samRecord.getAlignmentStart() + 1;
		length = PositionProvider.adjustWindowPos(pos, tmpLength, translator);
	}
	
	@Override
	public IntervalPosProvider build() {
		return new IntervalPosProvider(pos, length); 
	}
	
}
