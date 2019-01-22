package lib.data.cache.container;

import java.util.ArrayList;
import java.util.List;

import lib.util.coordinate.Coordinate;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperProcessor;

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
	public List<RecordWrapperProcessor> getCaches() {
		List<RecordWrapperProcessor> caches = new ArrayList<RecordWrapperProcessor>(10);
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
	public void preProcess() {
		forwardContainer.preProcess();
		reverseContainer.preProcess();
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		getCacheContainer(recordWrapper).process(recordWrapper);
	}
	
	@Override
	public void postProcess() {
		forwardContainer.postProcess();
		reverseContainer.postProcess();
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		switch (coordinate.getStrand()) {
		case FORWARD:
			forwardContainer.populate(container, coordinate);
			break;

		case REVERSE:
			reverseContainer.populate(container, coordinate);
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
