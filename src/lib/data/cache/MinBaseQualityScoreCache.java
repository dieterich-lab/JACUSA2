package lib.data.cache;

import htsjdk.samtools.AlignmentBlock;
import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

public class MinBaseQualityScoreCache<T extends AbstractData> 
implements Cache<T> {

	private final byte minBaseCallQuality;
	private final Cache<T> cache;
	
	public MinBaseQualityScoreCache(final byte minBaseCallQuality, final AbstractCache<T> cache) {
		this.minBaseCallQuality = minBaseCallQuality;
		this.cache = cache;
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			addRecordWrapperRegion(block.getReadStart(), block.getLength(), recordWrapper);
		}
	}

	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		if (recordWrapper.getSAMRecord().getBaseQualities()[readPosition] >= minBaseCallQuality) {
			cache.addRecordWrapperPosition(readPosition, recordWrapper);
		}
	}
	
	// TODO
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		for (int i = 0; i < length; ++i) {
			addRecordWrapperPosition(readPosition + i, recordWrapper);	
		}
	}
	
	public void clear() {
		cache.clear();
	}

	public T getData(final Coordinate coordinate) {
		return cache.getData(coordinate);
	}

	@Override
	public AbstractMethodFactory<T> getMethodFactory() {
		return cache.getMethodFactory();
	}
	
}
