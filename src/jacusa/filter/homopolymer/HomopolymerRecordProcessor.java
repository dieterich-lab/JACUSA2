package jacusa.filter.homopolymer;

import java.util.Collection;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.record.Record;
import lib.util.Base;

/**
 * This class process a read and identifies homopolymers within the aligned region. Base call quality
 * and base calls from insertions are ignored. Furthermore, homopolymers ONLY WITHIN 
 * alignment blocks are identified. Homopolymer that span splice sites are currently
 * ignored.
 *  
 * Tested in @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCacheTest
 */
public class HomopolymerRecordProcessor implements GeneralRecordProcessor {

	private final int minLength;
	
	private final HomopolymerStorage storage;
	
	public HomopolymerRecordProcessor(
			final int minLength,
			final HomopolymerStorage storage) {
		
		this.minLength	= minLength;
		this.storage 	= storage;
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final Record record) {
		final byte[] readBases = record.getSAMRecord().getReadBases(); 
		final List<AlignmentBlock> blocks = record.getSAMRecord()
				.getAlignmentBlocks();
		for (final AlignmentBlock block : blocks) {
			final int refPos 	= block.getReferenceStart();
			final int readPos 	= block.getReadStart() - 1; // -1 to refer to 0-index array
			final int length 	= block.getLength();
			
			final HomopolymerBuilder builder = new HomopolymerBuilder(refPos, getMinLength());
			for (int i = 0; i < length; ++i) {
				final Base base = Base.valueOf(readBases[readPos + i]);
				builder.add(base);
			}
			
			final Collection<Homopolymer> homopolymers = builder.build();
			for (final Homopolymer homopolymer : homopolymers) {
				storage.increment(homopolymer.getPosition(), homopolymer.getLength());
			}
		}
	}

	public int getMinLength() {
		return minLength;
	}
	
}
