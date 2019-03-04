package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPositionProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

public class RF_FIRSTSTRAND_LocationInterpreter 
implements LocationInterpreter {


	@Override
	public Position getArrestPosition(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		// SE -> use end of current read 
		if (! recordExtended.getSAMRecord().getReadPairedFlag()) {
			return getLastAlignmentPosition(recordExtended, translator);
		}
		final SAMRecordExtended mate = recordExtended.getMate();

		// PE -> use start of second mate
		final SAMRecord record = recordExtended.getSAMRecord();
		if (record.getSecondOfPairFlag()) {
			return getFirstAlignmentPosition(recordExtended, translator);
		}
		
		if (! mate.getSAMRecord().getMateUnmappedFlag()) {
			return getFirstAlignmentPosition(mate, translator);
		}
		// TODO what is the arrest position if second mate is not mapped?
		// fallback to SE? or there is just no arrest position
		return null;
	}

	@Override
	public PositionProvider getThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {

		// SE
		if (! recordExtended.getSAMRecord().getReadPairedFlag()) {
			final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
			final List<PositionProvider> positionProviders = new ArrayList<>(size);
			positionProviders.addAll(getThroughPositionProvider(0, size - 1, recordExtended, translator));
			positionProviders.add(getLastThroughPositionProvider(recordExtended, translator));
			return new CombinedPositionProvider(positionProviders);
		}
		
		// PE
		final SAMRecord record = recordExtended.getSAMRecord();
		if (record.getSecondOfPairFlag()) {
			final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
			final List<PositionProvider> positionProviders = new ArrayList<>(size);
			positionProviders.addAll(getThroughPositionProvider(0, size - 1, recordExtended, translator));
			positionProviders.add(getLastThroughPositionProvider(recordExtended, translator));
			return new CombinedPositionProvider(positionProviders);
		}
		return new AllAlignmentBlocksPositionProvider(recordExtended, translator);
	}
	
}
