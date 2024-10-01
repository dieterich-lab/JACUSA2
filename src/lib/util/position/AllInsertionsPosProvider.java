package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO add documentation
 */
public class AllInsertionsPosProvider implements PositionProvider {

	private final CombinedPositionProvider posProvider;
		
	public AllInsertionsPosProvider(
			final Record record, final CoordinateTranslator translator, final boolean onlyStart) {

		final List<Integer> cigarDetailIs = record.getInsertion();
		final int insertions = cigarDetailIs.size();
		final List<PositionProvider> posProviders = new ArrayList<>(insertions);
		
		for (int index = 0; index < insertions; ++index) {
			posProviders.add(
					new InsertionPositionProviderBuilder(index, record, translator, onlyStart)
					.adjustWindowPos()
					.build());
		}
		posProvider = new CombinedPositionProvider(posProviders);
	}

	@Override
	public boolean hasNext() {
		return posProvider.hasNext();
	}
	
	@Override
	public Position next() {
		return posProvider.next();
	}
			
}
