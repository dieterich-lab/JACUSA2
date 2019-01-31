package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.CollectionPositionProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

public class FR_SECONDSTRAND_LocationInterpreter 
implements LocationInterpreter {
	
	@Override
	public PositionProvider getArrestPositionProvider(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		return new CollectionPositionProvider(
				Arrays.asList(getFirstAlignmentPosition(recordExtended, translator)));
	}
	
	@Override
	public PositionProvider getThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		positionProviders.add(getFirstThroughPositionProvider(recordExtended, translator));
		positionProviders.addAll(getThroughPositionProvider(1, size - 1, recordExtended, translator));
		return new CombinedPositionProvider(positionProviders);
	}
	
}
