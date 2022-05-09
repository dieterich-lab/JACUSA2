package jacusa.filter.processrecord;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import java.util.List;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.record.ProcessedRecord;
import lib.util.position.AlgnBlockPosProviderBuilder;

/**
 * This class will mark all read start/end position +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in test.jacusa.filter.processrecord.ProcessReadStartEndTest
 */
public class ProcessReadStartEnd extends AbstractFilterRecordProcessor {
	
	public ProcessReadStartEnd(
			final SharedStorage sharedStorage, 
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}

	public void process(final ProcessedRecord record) {
		final SAMRecord samRecord = record.getSAMRecord();
		// note: alignmentBlock.getReadStart() is 1-indexed
		final List<AlignmentBlock> algnBlocks = samRecord.getAlignmentBlocks();

		// read start
		getPosProcessor().process(
				new AlgnBlockPosProviderBuilder(
						0, 
						record, 
						getTranslator())
				.tryFirst(getDistance())
				.adjustWinPos()
				.build());
		
		// read end
		getPosProcessor().process(
				new AlgnBlockPosProviderBuilder(
						algnBlocks.size() - 1, 
						record, 
						getTranslator())
				.tryLast(getDistance())
				.adjustWinPos()
				.build());
	}
	
}