package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AlignmentBlockPositionProviderBuilder;
import lib.util.position.AllAlignmentBlocksPositionProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

public class RF_FIRSTSTRAND_LocationInterpreter implements LocationInterpreter {

	@Override
	public boolean hasArrestPosition(SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		if (record.getReadPairedFlag()) {
			if (! record.getProperPairFlag()) {
				return false;
			}
			return record.getSecondOfPairFlag();
		}
		return true;
	}
	
	@Override
	public Position getArrestPosition(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		if (! recordExtended.getSAMRecord().getReadPairedFlag()) {
			return getArrestPositionSE(recordExtended, translator);
		}
		return getArrestPositionPE(recordExtended, translator);
	}
	
	private Position getArrestPositionSE(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		if (recordExtended.getSAMRecord().getReadNegativeStrandFlag()) {
			return getFirstAlignmentPosition(recordExtended, translator);
		}
		return getLastAlignmentPosition(recordExtended, translator);
	}
	
	private Position getArrestPositionPEhelper(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		if (! recordExtended.getSAMRecord().getReadNegativeStrandFlag()) {
			return getFirstAlignmentPosition(recordExtended, translator);
		}
		return getLastAlignmentPosition(recordExtended, translator);
	}
	
	private Position getArrestPositionPE(SAMRecordExtended recordExtended, CoordinateTranslator translator) {
		final SAMRecord record = recordExtended.getSAMRecord();

		if (! record.getProperPairFlag()) {
			return null;
		}
		
		if (record.getSecondOfPairFlag()) {
			return getArrestPositionPEhelper(recordExtended, translator);
		}
		
		final SAMRecordExtended mate = recordExtended.getMate();
		return getArrestPositionPEhelper(mate, translator);
	}

	@Override
	public PositionProvider getThroughPositionProvider(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {

		final SAMRecord record = recordExtended.getSAMRecord();
		
		if (! record.getReadPairedFlag()) {
			return getThroughPositionProviderSE(recordExtended, translator);
		}
		
		return getThroughPositionProviderPE(recordExtended, translator);
	}
		
	public PositionProvider getThroughPositionProviderSE(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		if (recordExtended.getSAMRecord().getReadNegativeStrandFlag()) {
			positionProviders.add(new AlignmentBlockPositionProviderBuilder(0, recordExtended, translator)
					.ignoreFirst(1)
					.adjustWindowPos()
					.build());
			positionProviders.addAll(getThroughPositionProvider(1, size - 1, recordExtended, translator));
		} else {
			positionProviders.addAll(getThroughPositionProvider(0, size - 1, recordExtended, translator));
			positionProviders.add(new AlignmentBlockPositionProviderBuilder(size - 1, recordExtended, translator)
					.ignoreLast(1)
					.adjustWindowPos()
					.build());			
		}
		return new CombinedPositionProvider(positionProviders);
	}
		
	public PositionProvider getThroughPositionProviderPE(
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		// per default not properly paired reads have no arrest site
		if (! recordExtended.getSAMRecord().getProperPairFlag()) {
			return new AllAlignmentBlocksPositionProvider(recordExtended, translator);
		}
		
		if (recordExtended.getSAMRecord().getSecondOfPairFlag()) {
			final int size = recordExtended.getSAMRecord().getAlignmentBlocks().size();
			final List<PositionProvider> positionProviders = new ArrayList<>(size);
			if (recordExtended.getSAMRecord().getReadNegativeStrandFlag()) {
				positionProviders.addAll(getThroughPositionProvider(0, size - 1, recordExtended, translator));
				positionProviders.add(new AlignmentBlockPositionProviderBuilder(size - 1, recordExtended, translator)
						.ignoreLast(1)
						.adjustWindowPos()
						.build());
			} else {
				positionProviders.add(new AlignmentBlockPositionProviderBuilder(0, recordExtended, translator)
						.ignoreFirst(1)
						.adjustWindowPos()
						.build());
				positionProviders.addAll(getThroughPositionProvider(1, size - 1, recordExtended, translator));							
			}
			return new CombinedPositionProvider(positionProviders);
		}

		return new AllAlignmentBlocksPositionProvider(recordExtended, translator);
	}
	
}
