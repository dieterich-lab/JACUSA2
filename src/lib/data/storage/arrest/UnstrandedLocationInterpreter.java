package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.CollectionPositionProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

public class UnstrandedLocationInterpreter implements LocationInterpreter {

	@Override
	public PositionProvider getArrestPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		return new CollectionPositionProvider(Arrays.asList(
				getFirstAlignmentPosition(recordExtended, translator),
				getLastAlignmentPosition(recordExtended, translator)));
	}
	
	@Override
	public PositionProvider getThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final SAMRecord record = recordExtended.getSAMRecord();
		final int size = record.getAlignmentBlocks().size();
		
		if (size == 1) {
			return getInnerThroughRegion(recordExtended, translator);
		} else {
			final List<PositionProvider> positionProviders = new ArrayList<>(size); 
			positionProviders.add(getFirstThroughPositionProvider(recordExtended, translator));
			positionProviders.addAll(getThroughPositionProvider(1, size - 2, recordExtended, translator));
			positionProviders.add(getLastThroughPositionProvider(recordExtended, translator));
			return new CombinedPositionProvider(positionProviders);
		}
	}

	
	
}
