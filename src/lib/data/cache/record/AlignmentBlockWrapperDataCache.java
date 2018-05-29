package lib.data.cache.record;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import htsjdk.samtools.AlignmentBlock;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.RegionDataCache;

public class AlignmentBlockWrapperDataCache<T extends AbstractData>
implements RecordWrapperDataCache<T> {

	private final RegionDataCache<T> dataCache;
	
	public AlignmentBlockWrapperDataCache(final RegionDataCache<T> regionDataCache) {
		this.dataCache = regionDataCache;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = alignmentBlock.getReferenceStart();
			final int readPosition = alignmentBlock.getReadStart() - 1;
			final int length = alignmentBlock.getLength();
			dataCache.addRegion(referencePosition, readPosition, length, recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		dataCache.addData(data, coordinate);
	}
	
	@Override
	public void clear() {
		dataCache.clear();
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return dataCache.getCoordinateController();
	}
	
}
