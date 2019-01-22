package jacusa.filter.homopolymer;

import java.util.Arrays;
import java.util.Collection;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.util.Base;

/**
 * TODO add comments
 * 
 * @param 
 */

public class HomopolymerReadFilterCache 
extends AbstractHomopolymerFilterCache
implements RecordWrapperProcessor {

	// indices of position in window is a homopolymer
	private final boolean[] isHomopolymer;
	
	public HomopolymerReadFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(c, filteredDataFetcher, minLength, sharedCache);
		isHomopolymer = new boolean[sharedCache.getCoordinateController().getActiveWindowSize()];
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		// we only consider consecutively aligned regions of a read
		// insertions are currently ignored
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart();
			final int readPosition 		= block.getReadStart() - 1;
			final int length 			= block.getLength();

			// only process if alignment block is bigger than minLength
			if (length >= getMinLength()) {
				processAlignmentBlock(referencePosition, readPosition, length, recordWrapper);
			}
		}
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}

	@Override
	protected boolean[] getIsHomopolymer() {
		return isHomopolymer;
	}
	
	@Override
	public void clear() {
		Arrays.fill(isHomopolymer, false);
	}
	
	/**
	 * Helper method.
	 * All positions are expected 0-indexed.
	 * 
	 * @param referencePosition	position on the the reference
	 * @param readPosition		position on the read
	 * @param length			length of block
	 * @param recordWrapper		read to be processed
	 */
	private void processAlignmentBlock(
			final int referencePosition, final int readPosition, 
			final int length, 
			final SAMRecordWrapper recordWrapper) {

		final SAMRecord record = recordWrapper.getSAMRecord();
		final HomopolymerBuilder builder = new HomopolymerBuilder(referencePosition, getMinLength()); 
		
		// within window
		for (int offset = 0; offset < length; ++offset) {
			final int currentReadPosition = readPosition + offset;
			final Base base = Base.valueOf(record.getReadBases()[currentReadPosition]);
			builder.add(base);
		}
		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}
	
}