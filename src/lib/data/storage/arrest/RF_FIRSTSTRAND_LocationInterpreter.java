package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.CollectionPositionProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

public class RF_FIRSTSTRAND_LocationInterpreter 
implements LocationInterpreter {


	@Override
	public PositionProvider getArrestPositionProvider(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		final Position pos = getLastAlignmentPosition(recordExtended, translator);
		if (pos == null) {
			return new CollectionPositionProvider(new ArrayList<>(0));
		}
		return new CollectionPositionProvider(
				Arrays.asList(pos));
	}

	@Override
	public PositionProvider getThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		positionProviders.addAll(getThroughPositionProvider(0, size - 1, recordExtended, translator));
		positionProviders.add(getLastThroughPositionProvider(recordExtended, translator));
		return new CombinedPositionProvider(positionProviders);
	}
	
}