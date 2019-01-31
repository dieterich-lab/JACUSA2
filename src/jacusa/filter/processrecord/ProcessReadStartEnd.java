package jacusa.filter.processrecord;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import java.util.List;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.util.position.AlignmentBlockBuilder;
import lib.recordextended.SAMRecordExtended;

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
				new AlignmentBlockBuilder(
						0, 
						recordExtended, 
						getTranslator())
				.tryFirst(getDistance())
				.build());
		
		// read end
		getPositionProcessor().process(
				new AlignmentBlockBuilder(
						alignmentBlocks.size() - 1, 
						recordExtended, 
						getTranslator())
				.tryLast(getDistance())
				.build());
	}
	
}