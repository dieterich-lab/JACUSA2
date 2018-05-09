package jacusa.filter.cache;

import java.util.Arrays;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractDataCache;
import lib.data.cache.record.RecordDataCache;
import lib.data.has.filter.HasBooleanFilterData;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments
 * 
 * @param <T>
 */
// FIXME make me more efficient - share homopolymer information between BAM(s)
public class HomopolymerFilterCache<T extends AbstractData & HasBooleanFilterData> 
extends AbstractDataCache<T>
implements RecordDataCache<T> {
	
	private final char c;
	
	// min length of identical base call to define homopolymer
	private final int minLength;

	private final BaseCallConfig baseCallConfig;

	// indices of position in window is a homopolymer
	private final boolean[] isHomopolymer;
	
	public HomopolymerFilterCache(final char c,
			final int minLength, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(coordinateController);
		
		this.c				= c;
		this.minLength 		= minLength;
		this.baseCallConfig = baseCallConfig;
		isHomopolymer 		= new boolean[coordinateController.getActiveWindowSize()];
	}

	@Override
	public void addRecord(final SAMRecordWrapper recordWrapper) {
		// TODO we only consider consecutively aligned regions of a read
		// insertions are currently ignored
		for (AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart();
			final int readPosition 		= block.getReadStart() - 1;
			final int length 			= block.getLength();

			// only process if alignment block is bigger than min. length of 
			// a homopolymer
			if (length >= minLength) {
				processAlignmentBlock(referencePosition, readPosition, length, recordWrapper);
			}
		}
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
	private void processAlignmentBlock(final int referencePosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}

		// make sure coordinates are within window
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}

		// unwrap read
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		// the current length of putative homopolymer region (PHR) 
		int polymerLength = 0;
		// the first position of PHR
		int firstReferencePosition = -1;
		// base call of PHR
		// next base call must identical to expand PHR
		int lastBaseIndex = -1;

		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int currentReadPosition = windowPositionGuard.getReadPosition() + j;
			
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[currentReadPosition]);

			if (baseIndex < 0) { // base call = "N"
				// mark region if current PHR is long enough 
				if (polymerLength >= minLength) {
					markRegion(firstReferencePosition, polymerLength);
				}
				// reset
				polymerLength = 0;
				firstReferencePosition = -1;
				lastBaseIndex = -1;
			} else if (baseIndex == lastBaseIndex) { // base call match - enlarge current PHR
				polymerLength++;
			} else { // base call mismatch - end current PHR
				// mark region if current PHR is long enough
				if (polymerLength >= minLength) {
					markRegion(firstReferencePosition, polymerLength);
				}
				// start new pHR
				polymerLength = 1;
				firstReferencePosition = windowPositionGuard.getReferencePosition() + j;
				lastBaseIndex = baseIndex;
			}
		}
	}

	/**
	 * Helper method. Marks a region within a window defined by firstReferencePosition and length. 
	 * 
	 * @param firstReferencePosition	start position of region
	 * @param length					length of region (non-inclusive)
	 */
	private void markRegion(final int firstReferencePosition, final int length) {
		final int windowPosition = getCoordinateController().convert2windowPosition(firstReferencePosition);
		for (int i = 0; i < length; ++i) {
			isHomopolymer[windowPosition + i] = true;
		}
	}

	@Override
	public void clear() {
		Arrays.fill(isHomopolymer, false);
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (windowPosition < 0) {
			return;
		}

		data.getBooleanFilterData().add(c, isHomopolymer[windowPosition]);
	}
	
}