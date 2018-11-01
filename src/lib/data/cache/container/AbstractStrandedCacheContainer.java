package lib.data.cache.container;

import java.util.ArrayList;
import java.util.List;

import lib.util.coordinate.Coordinate;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperDataCache;

public abstract class AbstractStrandedCacheContainer 
implements CacheContainer {

	private final CacheContainer forwardContainer; 
	private final CacheContainer reverseContainer;
	
	public AbstractStrandedCacheContainer(
			final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {

		this.forwardContainer	= forwardContainer;
		this.reverseContainer 	= reverseContainer;
	}
	
	@Override
	public ReferenceProvider getReferenceProvider() {
		return forwardContainer.getReferenceProvider();
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
	public List<RecordWrapperDataCache> getCaches() {
		List<RecordWrapperDataCache> caches = new ArrayList<RecordWrapperDataCache>(10);
		caches.addAll(forwardContainer.getCaches());
		caches.addAll(reverseContainer.getCaches());
		return caches;
	}
	
	@Override
	public void clear() {
		forwardContainer.clear();
		reverseContainer.clear();
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		getCacheContainer(recordWrapper).process(recordWrapper);
	}
	
	@Override
	public void populateContainer(DataTypeContainer container, Coordinate coordinate) {
		switch (coordinate.getStrand()) {
		case FORWARD:
			forwardContainer.populateContainer(container, coordinate);
			break;

		case REVERSE:
			reverseContainer.populateContainer(container, coordinate);
			break;

		case UNKNOWN:
			throw new IllegalArgumentException("Unstranded coordinates not supported!");
		}
	}
	
	protected abstract CacheContainer getCacheContainer(final SAMRecordWrapper recordWrapper);
	
	public CacheContainer getForwardContainer() {
		return forwardContainer;
	}

	public CacheContainer getReverseContainer() {
		return reverseContainer;
	}
	
}
