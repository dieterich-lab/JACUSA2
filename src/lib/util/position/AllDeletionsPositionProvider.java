package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.recordextended.SAMRecordExtended;
import lib.util.coordinate.CoordinateTranslator;

public class AllDeletionsPositionProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public AllDeletionsPositionProvider(
			final SAMRecordExtended recordExtended, final CoordinateTranslator translator) {

		final List<Integer> cigarElementExtendedIndices = recordExtended.getDeletion();
		final int deletions = cigarElementExtendedIndices.size();
		final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>(deletions);
		
		for (int index = 0; index < deletions; ++index) {
			positionProviders.add(
					new DeletionPositionProviderBuilder(index, recordExtended, translator)
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
