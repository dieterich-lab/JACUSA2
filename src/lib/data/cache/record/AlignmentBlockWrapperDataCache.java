package lib.data.cache.record;

import lib.util.coordinate.Coordinate;
import htsjdk.samtools.AlignmentBlock;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.RegionDataCache;

public class AlignmentBlockWrapperDataCache
implements RecordWrapperDataCache {

	private final RegionDataCache dataCache;
	
	public AlignmentBlockWrapperDataCache(final RegionDataCache regionDataCache) {
		this.dataCache = regionDataCache;
	}

	@Override
	public void processRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = alignmentBlock.getReferenceStart();
			final int readPosition = alignmentBlock.getReadStart() - 1;
			final int length = alignmentBlock.getLength();
			dataCache.addRegion(referencePosition, readPosition, length, recordWrapper);
		}
	}

	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		dataCache.populate(container, coordinate);
	}
	
	@Override
	public void clear() {
		dataCache.clear();
	}
	
}
