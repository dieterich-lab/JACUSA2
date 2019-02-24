package jacusa.filter.processrecord;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import java.util.List;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.AlignmentBlockPositionProviderBuilder;

/**
 * Tested in test.jacusa.filter.processrecord.ProcessReadStartEndTest
 */
public class ProcessReadStartEnd extends AbstractFilterRecordExtendedProcessor {
	
	public ProcessReadStartEnd(
			final SharedStorage sharedStorage, 
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}

	public void process(final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		// note: alignmentBlock.getReadStart() is 1-indexed
		final List<AlignmentBlock> alignmentBlocks = record.getAlignmentBlocks();

		// read start
		getPositionProcessor().process(
				new AlignmentBlockPositionProviderBuilder(
						0, 
						recordExtended, 
						getTranslator())
				.tryFirst(getDistance())
				.adjustWindowPos()
				.build());
		
		// read end
		getPositionProcessor().process(
				new AlignmentBlockPositionProviderBuilder(
						alignmentBlocks.size() - 1, 
						recordExtended, 
						getTranslator())
				.tryLast(getDistance())
				.adjustWindowPos()
				.build());
	}
	
}