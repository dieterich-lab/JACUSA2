package lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AlgnBlockPosProviderBuilder;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.CombinedPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.util.position.UnmodifiablePosition;

public class RevForFirstStrandLocInterpreter implements LocationInterpreter {

	@Override
	public boolean hasArrestPosition(Record record) {
		final SAMRecord samRecord = record.getSAMRecord();
		if (samRecord.getReadPairedFlag()) {
			if (! samRecord.getProperPairFlag()) {
				return false;
			}
			return samRecord.getSecondOfPairFlag();
		}
		return true;
	}
	
	@Override
	public Position getArrestPosition(Record record, CoordinateTranslator translator) {
		if (! record.getSAMRecord().getReadPairedFlag()) {
			return getArrestPositionSE(record, translator);
		}
		return getArrestPositionPE(record, translator);
	}
	
	private Position getArrestPositionSE(Record record, CoordinateTranslator translator) {
		if (record.getSAMRecord().getReadNegativeStrandFlag()) {
			return getFirstAlignmentPosition(record, translator);
		}
		return getLastAlignmentPosition(record, translator);
	}
	
	private Position getArrestPositionPEhelper(Record record, CoordinateTranslator translator) {
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
	
	private Position getArrestPositionPE(Record record, CoordinateTranslator translator) {
		final SAMRecord samRecord = record.getSAMRecord();

		if (! samRecord.getProperPairFlag()) {
			return null;
		}
		
		if (samRecord.getSecondOfPairFlag()) {
			if (samRecord.getReadNegativeStrandFlag()) {
				return getLastAlignmentPosition(record, translator);
			}
			return getFirstAlignmentPosition(record, translator);
		}
		
		return getArrestPositionPEhelper(record, translator);
	}

	@Override
	public PositionProvider getThroughPositionProvider(
			final Record record,
			final CoordinateTranslator translator) {

		final SAMRecord samRecord = record.getSAMRecord();
		
		if (! samRecord.getReadPairedFlag()) {
			return getThroughPositionProviderSE(record, translator);
		}
		
		return getThroughPositionProviderPE(record, translator);
	}
		
	public PositionProvider getThroughPositionProviderSE(
			final Record record,
			final CoordinateTranslator translator) {
		
		final int size = record.getSAMRecord().getAlignmentBlocks().size();
		final List<PositionProvider> positionProviders = new ArrayList<>(size);
		if (record.getSAMRecord().getReadNegativeStrandFlag()) {
			positionProviders.add(new AlgnBlockPosProviderBuilder(0, record, translator)
					.ignoreFirst(1)
					.adjustWindowPos()
					.build());
			positionProviders.addAll(getThroughPositionProvider(1, size - 1, record, translator));
		} else {
			positionProviders.addAll(getThroughPositionProvider(0, size - 1, record, translator));
			positionProviders.add(new AlgnBlockPosProviderBuilder(size - 1, record, translator)
					.ignoreLast(1)
					.adjustWindowPos()
					.build());			
		}
		return new CombinedPositionProvider(positionProviders);
	}
		
	public PositionProvider getThroughPositionProviderPE(
			final Record record,
			final CoordinateTranslator translator) {
		
		// per default not properly paired reads have no arrest site
		if (! record.getSAMRecord().getProperPairFlag()) {
			return new AllAlignmentBlocksPosProvider(record, translator);
		}
		
		if (record.getSAMRecord().getSecondOfPairFlag()) {
			final int size = record.getSAMRecord().getAlignmentBlocks().size();
			final List<PositionProvider> positionProviders = new ArrayList<>(size);
			if (record.getSAMRecord().getReadNegativeStrandFlag()) {
				positionProviders.addAll(getThroughPositionProvider(0, size - 1, record, translator));
				positionProviders.add(new AlgnBlockPosProviderBuilder(size - 1, record, translator)
						.ignoreLast(1)
						.adjustWindowPos()
						.build());
			} else {
				positionProviders.add(new AlgnBlockPosProviderBuilder(0, record, translator)
						.ignoreFirst(1)
						.adjustWindowPos()
						.build());
				positionProviders.addAll(getThroughPositionProvider(1, size - 1, record, translator));							
			}
			return new CombinedPositionProvider(positionProviders);
		}

		return new AllAlignmentBlocksPosProvider(record, translator);
	}
	
}
