package jacusa.filter.homopolymer;

import java.util.Collection;

import htsjdk.samtools.AlignmentBlock;
import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.util.Base;
import lib.recordextended.SAMRecordExtended;

/**
 * This class process a read and identifies homopolymers within the aligned region. Base call quality
 * and base calls from insertions are ignored.
 *  
 * Tested in @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCacheTest
 */
public class HomopolymerReadRecordProcessor implements RecordExtendedPrePostProcessor {

	private final int minLength;
	
	private final HomopolymerStorage storage;
	
	public HomopolymerReadRecordProcessor(
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
	public void process(final SAMRecordExtended recordExtended) {
		final byte[] readBases = recordExtended.getSAMRecord().getReadBases(); 
		for (final AlignmentBlock block : recordExtended.getSAMRecord().getAlignmentBlocks()) {
			final int refPos 	= block.getReferenceStart();
			final int readPos 	= block.getReadStart() - 1;
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
