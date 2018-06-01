package lib.data.cache.container;

import java.util.ArrayList;
import java.util.List;

import lib.util.coordinate.Coordinate;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperDataCache;

public abstract class AbstractStrandedCacheContainer<T extends AbstractData> 
implements CacheContainer<T> {

	private final CacheContainer<T> forwardContainer; 
	private final CacheContainer<T> reverseContainer;
	
	public AbstractStrandedCacheContainer(
			final CacheContainer<T> forwardContainer, 
			final CacheContainer<T> reverseContainer) {

		this.forwardContainer	= forwardContainer;
		this.reverseContainer 	= reverseContainer;
	}
	
	@Override
	public int getNext(int windowPosition) {
		final int forwardNext = forwardContainer.getNext(windowPosition);
		final int reverseNext = reverseContainer.getNext(windowPosition);

		int res = -1;
		if (forwardNext == -1 || reverseNext == -1) {
			res = Math.max(forwardNext, reverseNext);
		} else {
			res = Math.min(forwardNext, reverseNext);
		}

		return res;
	}
	
	@Override
	public List<RecordWrapperDataCache<T>> getDataCaches() {
		List<RecordWrapperDataCache<T>> caches = new ArrayList<RecordWrapperDataCache<T>>(10);
		caches.addAll(forwardContainer.getDataCaches());
		caches.addAll(reverseContainer.getDataCaches());
		return caches;
	}
	
	@Override
	public void clear() {
		forwardContainer.clear();
		reverseContainer.clear();
	}
	
	@Override
	public void add(final SAMRecordWrapper recordWrapper) {
		getCacheContainer(recordWrapper).add(recordWrapper);
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		switch (coordinate.getStrand()) {
		case FORWARD:
			forwardContainer.addData(data, coordinate);
			break;

		case REVERSE:
			reverseContainer.addData(data, coordinate);
			break;

		case UNKNOWN:
			throw new IllegalArgumentException("Unstranded coordinates not supported!");
		}
	}
	
	protected abstract CacheContainer<T> getCacheContainer(final SAMRecordWrapper recordWrapper);
	
	public CacheContainer<T> getForwardContainer() {
		return forwardContainer;
	}

	public CacheContainer<T> getReverseContainer() {
		return reverseContainer;
	}
	
}
