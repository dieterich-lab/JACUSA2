package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import lib.record.Record;
import lib.util.LibraryType;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AlgnBlockPosProviderBuilder;
import lib.util.position.UnmodifiablePosition;
import lib.util.position.MatchPosition;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO add documentation
 */
public interface LocationInterpreter {

	Position getArrestPosition(Record record, CoordinateTranslator translator);
	
	PositionProvider getThroughPositionProvider(
			Record record, CoordinateTranslator translator);
	
	boolean hasArrestPosition(Record record);
	
	default boolean isArrestPosition(final Position position, final CoordinateTranslator translator) {
		final Position arrestPos = getArrestPosition(position.getRecord(), translator);
		if (arrestPos == null) {
			return false;
		}
		return arrestPos.equals(position);
	}
	
	static LocationInterpreter create(final LibraryType libraryType) {
		switch (libraryType) {

		case RF_FIRSTSTRAND:
			return new RevForFirstStrandLocInterpreter();

		case FR_SECONDSTRAND:
			return new ForRevSecondStrandlocInterpreter();
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
	}
	
	default Position getFirstAlignmentPosition(
			final Record record, 
			final CoordinateTranslator translator) {
		
		Position pos = new MatchPosition.Builder(0, record, translator).build();
		return new UnmodifiablePosition(pos);
	}
	
	default Position getLastAlignmentPosition(
			final Record record, 
			final CoordinateTranslator translator) {
		
		final int size 				= record.getSAMRecord().getAlignmentBlocks().size() - 1;
		final AlignmentBlock block 	= record.getSAMRecord().getAlignmentBlocks().get(size);
		final int length			= block.getLength() - 1;
		final int refPos 			= block.getReferenceStart() + length;
		final int readPos			= block.getReadStart() - 1 + length;
		final int winPos			= translator.ref2winPos(refPos);
		return new UnmodifiablePosition(refPos, readPos, winPos, record);
	}
	
	default List<PositionProvider> getThroughPositionProvider(
			final int startIndex, 
			final int size, 
			final Record record,
			final CoordinateTranslator translator) {
		
		final int endIndex = startIndex + size;
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		for (int i = startIndex; i < endIndex; ++i) {
			positionProviders.add(
					new AlgnBlockPosProviderBuilder(
							i, record, translator)
					.adjustWinPos()
					.build()); 
		}
		return positionProviders;
	}
	
	
}
