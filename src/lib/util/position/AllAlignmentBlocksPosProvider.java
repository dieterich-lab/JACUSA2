package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO add documentation
 */
public class AllAlignmentBlocksPosProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public AllAlignmentBlocksPosProvider(
			final Record record, final CoordinateTranslator translator) {

		final List<AlignmentBlock> blocks = record.getSAMRecord().getAlignmentBlocks();
		final int blocksSize = blocks.size();
		final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>(blocksSize);
		
		for (int blockIndex = 0; blockIndex < blocksSize; ++blockIndex) {
			positionProviders.add(
					new AlgnBlockPosProviderBuilder(blockIndex, record, translator)
					.adjustWinPos()
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
