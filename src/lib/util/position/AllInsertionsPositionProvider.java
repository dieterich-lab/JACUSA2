package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.recordextended.SAMRecordExtended;
import lib.util.coordinate.CoordinateTranslator;

public class AllInsertionsPositionProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public AllInsertionsPositionProvider(
			final SAMRecordExtended recordExtended, final CoordinateTranslator translator) {

		final List<Integer> cigarElementExtendedIndices = recordExtended.getInsertion();
		final int insertions = cigarElementExtendedIndices.size();
		final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>(insertions);
		
		for (int index = 0; index < insertions; ++index) {
			positionProviders.add(
					new InsertionPositionProviderBuilder(index, recordExtended, translator)
					.adjustWindowPos()
					.build());
		}
		positionProvider = new CombinedPositionProvider(positionProviders);
	}

	@Override
	public boolean hasNext() {
		return positionProvider.hasNext();
	}
	
	@Override
	public Position next() {
		return positionProvider.next();
	}
			
}
