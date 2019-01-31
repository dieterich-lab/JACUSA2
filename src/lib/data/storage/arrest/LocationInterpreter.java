package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import lib.util.LibraryType;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AlignmentBlockBuilder;
import lib.util.position.MatchPosition;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

// TODO test
public interface LocationInterpreter {

	PositionProvider getArrestPositionProvider(
			SAMRecordExtended recordExtended, CoordinateTranslator translator);
	
	PositionProvider getThroughPositionProvider(
			SAMRecordExtended recordExtended, CoordinateTranslator translator);
	
	default boolean isArrest(
			final int readPos, SAMRecordExtended recordExtended, 
			CoordinateTranslator translator) {

		final PositionProvider positionProvider = 
				getArrestPositionProvider(recordExtended, translator);
		while (positionProvider.hasNext()) {
			final Position pos = positionProvider.next();
			if (pos.getReadPosition() == readPos) {
				return true;
			}
		}
		return false;
	}
	
	static LocationInterpreter create(final LibraryType libraryType) {
		switch (libraryType) {

		case UNSTRANDED:
			return new UnstrandedLocationInterpreter();

		case RF_FIRSTSTRAND:
			return new RF_FIRSTSTRAND_LocationInterpreter();

		case FR_SECONDSTRAND:
			return new FR_SECONDSTRAND_LocationInterpreter();
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
	}
	
	default Position getFirstAlignmentPosition(
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		return new MatchPosition.Builder(0, recordExtended, translator).build();
	}
	
	default Position getLastAlignmentPosition(
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
		return new MatchPosition.Builder(size, recordExtended, translator).build();
	}
	
	default PositionProvider getFirstThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		return new AlignmentBlockBuilder(0, recordExtended, translator)
				.ignoreFirst(1)
				.build();
	}
	
	default PositionProvider getInnerThroughRegion(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		return new AlignmentBlockBuilder(0, recordExtended, translator)
				.ignoreFirst(1)
				.ignoreLast(1)
				.build();
	}
	
	default PositionProvider getLastThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final List<AlignmentBlock> blocks = recordExtended.getSAMRecord().getAlignmentBlocks();
		final int size = blocks.size();
		return new AlignmentBlockBuilder(size - 1, recordExtended, translator)
				.ignoreLast(1)
				.build();
	}
	
	default List<PositionProvider> getThroughPositionProvider(
			final int startIndex, 
			final int size, 
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final int endIndex = startIndex + size;
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		for (int i = startIndex; i < endIndex; ++i) {
			positionProviders.add(
					new AlignmentBlockBuilder(
							i, recordExtended, translator).build()); 
		}
		return positionProviders;
	}
	
	
}
