package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.record.ProcessedRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AlgnBlockPosProviderBuilder;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.util.position.UnmodifiablePosition;

public class ForRevSecondStrandlocInterpreter 
implements LocationInterpreter {

	@Override
	public boolean hasArrestPosition(ProcessedRecord record) {
		final SAMRecord samRecord = record.getSAMRecord();
		if (samRecord.getReadPairedFlag()) {
			if (! samRecord.getProperPairFlag()) {
				return false;
			}
			return samRecord.getFirstOfPairFlag();
		}
		return true;
	}
	
	@Override
	public Position getArrestPosition(ProcessedRecord record, CoordinateTranslator translator) {
		if (! record.getSAMRecord().getReadPairedFlag()) {
			return getArrestPositionSE(record, translator);
		}
		return getArrestPositionPE(record, translator);
	}
	
	private Position getArrestPositionSE(ProcessedRecord record, CoordinateTranslator translator) {
		if (record.getSAMRecord().getReadNegativeStrandFlag()) {
			return getLastAlignmentPosition(record, translator);
		}
		return getFirstAlignmentPosition(record, translator);
	}
		
	private Position getArrestPositionPEhelper(ProcessedRecord record, CoordinateTranslator translator) {
		final SAMRecord samRecord = record.getSAMRecord();
		int refPos = -1;
		if (samRecord.getReadNegativeStrandFlag()) {
			refPos = samRecord.getMateAlignmentStart();
		} else {
			refPos = samRecord.getAlignmentStart() + Math.abs(samRecord.getInferredInsertSize()) - 1;
		}
		final int winPos = translator.ref2winPos(refPos);
		return new UnmodifiablePosition(refPos, -1, winPos, null);
	}
	
	private Position getArrestPositionPE(ProcessedRecord record, CoordinateTranslator translator) {
		final SAMRecord samRecord = record.getSAMRecord();

		if (! samRecord.getProperPairFlag()) {
			return null;
		}
		
		if (samRecord.getFirstOfPairFlag()) {
			return getArrestPositionSE(record, translator);
		}
		
		return getArrestPositionPEhelper(record, translator);
	}
	
	@Override
	public PositionProvider getThroughPositionProvider(
			final ProcessedRecord record,
			final CoordinateTranslator translator) {
		
		final SAMRecord samRecord = record.getSAMRecord();
		
		if (! samRecord.getReadPairedFlag()) {
			return getThroughPositionProviderSE(record, translator);
		}
		
		return getThroughPositionProviderPE(record, translator);
	}
		
	public PositionProvider getThroughPositionProviderSE(
			final ProcessedRecord record,
			final CoordinateTranslator translator) {
	
		final int size = record.getSAMRecord().getAlignmentBlocks().size();
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		if (record.getSAMRecord().getReadNegativeStrandFlag()) {
			positionProviders.addAll(getThroughPositionProvider(0, size - 1, record, translator));
			positionProviders.add(new AlgnBlockPosProviderBuilder(size - 1, record, translator)
					.ignoreLast(1)
					.adjustWinPos()
					.build());
		} else {
			positionProviders.add(new AlgnBlockPosProviderBuilder(0, record, translator)
					.ignoreFirst(1)
					.adjustWinPos()
					.build());
			positionProviders.addAll(getThroughPositionProvider(1, size - 1, record, translator));
		}
		return new CombinedPositionProvider(positionProviders);
	}
	
	public PositionProvider getThroughPositionProviderPE(
			final ProcessedRecord record,
			final CoordinateTranslator translator) {
		
		// per default not properly paired reads have no arrest site
		if (! record.getSAMRecord().getProperPairFlag()) {
			return new AllAlignmentBlocksPosProvider(record, translator);
		}
		
		if (record.getSAMRecord().getFirstOfPairFlag()) {
			final int size = record.getSAMRecord().getAlignmentBlocks().size();
			final List<PositionProvider> positionProviders = new ArrayList<>(size);
			if (record.getSAMRecord().getReadNegativeStrandFlag()) {
				positionProviders.addAll(getThroughPositionProvider(0, size - 1, record, translator));
				positionProviders.add(new AlgnBlockPosProviderBuilder(size - 1, record, translator)
						.ignoreLast(1)
						.adjustWinPos()
						.build());
			} else {
				positionProviders.add(new AlgnBlockPosProviderBuilder(0, record, translator)
						.ignoreFirst(1)
						.adjustWinPos()
						.build());
				positionProviders.addAll(getThroughPositionProvider(1, size - 1, record, translator));
			}
			return new CombinedPositionProvider(positionProviders);
		}

		return new AllAlignmentBlocksPosProvider(record, translator);
	}

}
