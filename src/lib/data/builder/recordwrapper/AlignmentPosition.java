package lib.data.builder.recordwrapper;

import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.util.Copyable;

public class AlignmentPosition implements Copyable<AlignmentPosition>{
	
	private final List<AlignmentBlock> blocks;
	
	private int index;
	private int offset;
	
	public AlignmentPosition(final List<AlignmentBlock> blocks) {
		this.blocks = blocks;
		
		index = 0;
		offset = 0;
	}
	
	private AlignmentPosition(
			final List<AlignmentBlock> blocks,
			final int index, final int offset) {

		this.blocks = blocks;
		
		this.index = index;
		this.offset = offset;
	}
	
	private AlignmentBlock getBlock(final int index) {
		return blocks.get(index);
	}

	public void advance(int matches) {
		for (int i = 0; i < matches; ++i) {
			if (offset == getBlock(index).getLength()) {
				if (index < blocks.size()) {
					offset = 0;
					++index;
				} else {
					// error
				}
			} else {
				++offset;
			}
		}
	}
	
	public int getReadPosition() {
		return getBlock(index).getReadStart() + offset - 1;
	}
	
	public int getReferencePosition() {
		return getBlock(index).getReferenceStart() + offset;
	}

	@Override
	public AlignmentPosition copy() {
		return new AlignmentPosition(blocks, index, offset);
	}
	
	static public AlignmentPosition createrReadPosition(final SAMRecord record, final int readPos) {
		final List<AlignmentBlock> blocks = record.getAlignmentBlocks();

		for (int index = 0; index < blocks.size(); ++index)
		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int readStart = block.getReadStart();
			if (readPos >= readStart && readPos <= readStart + block.getLength() - 1) {
				final int offset = readPos - readStart;
				return new AlignmentPosition(blocks, index, offset);
			}
		}

		throw new IllegalStateException("Are you kidding me?");
	}
	
	static public AlignmentPosition createrReferencePosition(final SAMRecord record, final int refPos) {
		final List<AlignmentBlock> blocks = record.getAlignmentBlocks();

		for (int index = 0; index < blocks.size(); ++index)
		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int refStart = block.getReferenceStart();
			if (refStart >= refStart && refStart <= refStart + block.getLength() - 1) {
				final int offset = refPos - refStart;
				return new AlignmentPosition(blocks, index, offset);
			}
		}

		throw new IllegalStateException("Are you kidding me?");
	}
	
}