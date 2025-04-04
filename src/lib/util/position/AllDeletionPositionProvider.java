package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

/**
 * DOCUMENT
 */
public class AllDeletionPositionProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public AllDeletionPositionProvider(
			final Record record,
			final CoordinateTranslator translator) {

		final List<Integer> cigarDetailIndexes = record.getDeletion();
		final int deletions = cigarDetailIndexes.size();
		final List<PositionProvider> positionProviders = new ArrayList<>(deletions);
		
		for (int index = 0; index < deletions; ++index) {
			positionProviders.add(
					new DeletionPositionProviderBuilder(index, record, translator)
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
