package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
 */
public class AllDeletionsPositionProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public AllDeletionsPositionProvider(
			final Record record, final CoordinateTranslator translator) {

		final List<Integer> cigarElementExtendedIndices = record.getDeletion();
		final int deletions = cigarElementExtendedIndices.size();
		final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>(deletions);
		
		for (int index = 0; index < deletions; ++index) {
			positionProviders.add(
					new DeletionPosProviderBuilder(index, record, translator)
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
