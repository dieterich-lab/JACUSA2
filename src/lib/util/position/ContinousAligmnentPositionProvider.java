package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import lib.recordextended.SAMRecordExtended;
import lib.util.coordinate.CoordinateTranslator;

public class ContinousAligmnentPositionProvider implements PositionProvider {

	private final CombinedPositionProvider positionProvider;
		
	public ContinousAligmnentPositionProvider(
			final SAMRecordExtended recordExtended, final CoordinateTranslator translator) {

		final List<AlignmentBlock> blocks = recordExtended.getSAMRecord().getAlignmentBlocks();
		final int blocksSize = blocks.size();
		final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>(blocksSize);
		
		for (int blockIndex = 0; blockIndex < blocksSize; ++blockIndex) {
			// TODO check alignmentBlock refPos is outside of window
			positionProviders.add(
					new AlignmentBlockBuilder(blockIndex, recordExtended, translator)
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
