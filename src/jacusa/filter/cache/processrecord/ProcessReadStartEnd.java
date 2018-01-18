package jacusa.filter.cache.processrecord;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;


import java.util.List;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.UniqueBaseCallDataCache;

public class ProcessReadStartEnd extends AbstractProcessRecord {

	public ProcessReadStartEnd(final int distance, final UniqueBaseCallDataCache<?> uniqueBaseCallCache) {
		super(distance, uniqueBaseCallCache);
	}

	public void processRecord(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		// note: alignmentBlock.getReadStart() is 1-indexed
		final List<AlignmentBlock> alignmentBlocks = record.getAlignmentBlocks();

		// read start
		final AlignmentBlock firstBlock = alignmentBlocks.get(0);
		getCache().addRecordWrapperRegion(firstBlock.getReadStart() - 1, Math.min(getDistance(), firstBlock.getLength()), recordWrapper);
		
		// read end
		final AlignmentBlock lastBlock = alignmentBlocks.get(alignmentBlocks.size() - 1);
		final int length = Math.min(getDistance(), lastBlock.getLength());
		getCache().addRecordWrapperRegion(lastBlock.getReadStart() - 1 + lastBlock.getLength() - 1 - length, length, recordWrapper);	
	}
	
}