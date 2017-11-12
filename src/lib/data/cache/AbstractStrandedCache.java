package lib.data.cache;

import lib.util.Coordinate;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.generator.DataGenerator;

public abstract class AbstractStrandedCache<T extends AbstractData> 
implements Cache<T> {

	private Cache<T> forward; 
	private Cache<T> reverse;

	public AbstractStrandedCache(final Cache<T> forward, final Cache<T> reverse) {
		if (forward.getDataGenerator() != reverse.getDataGenerator()) {
			throw new IllegalStateException("Forward and reverse Cache have different AbstractMethodFactory");
		}

		this.forward = forward;
		this.reverse = reverse;
	}
	
	@Override
	public void clear() {
		forward.clear();
		reverse.clear();
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		getCache(recordWrapper).addRecordWrapper(recordWrapper);
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		getCache(recordWrapper).addRecordWrapperPosition(readPosition, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		final Cache<T> cache = getCache(recordWrapper);
		for (int i = 0; i < length; ++i) {
			cache.addRecordWrapperRegion(readPosition, length, recordWrapper);
		}
	}
	
	@Override
	public T getData(Coordinate coordinate) {
		switch (coordinate.getStrand()) {
		case FORWARD:
			return forward.getData(coordinate);

		case REVERSE:
			return reverse.getData(coordinate);

		case UNKNOWN:
			throw new IllegalArgumentException("Unstranded coordinates not supported!");
		}
		
		return null;
	}
	
	protected abstract Cache<T> getCache(final SAMRecordWrapper recordWrapper);
	
	public Cache<T> getForward() {
		return forward;
	}

	public Cache<T> getReverse() {
		return reverse;
	}
	
	@Override
	public DataGenerator<T> getDataGenerator() {
		if (forward.getDataGenerator() != reverse.getDataGenerator()) {
			throw new IllegalStateException("Forward and reverse Cache have different AbstractMethodFactory");
		}
		return forward.getDataGenerator();
	}
	
}
