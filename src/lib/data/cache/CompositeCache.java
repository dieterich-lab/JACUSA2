package lib.data.cache;

import java.util.List;

import lib.util.Coordinate;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;

// FIXME is this even possible?
public class CompositeCache extends AbstractCache {

	private final List<AbstractCache<?>> caches;
	
	public CompositeCache(final int activeWindowSize, final List<AbstractCache<?>> caches) {
		super(activeWindowSize);
		this.caches = caches;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (AbstractCache<?> cache : caches) {
			cache.addRecordWrapper(recordWrapper);
		}
	}

	@Override
	public void clear() {
		for (AbstractCache<?> cache : caches) {
			cache.clear();
		} 
	}

	@Override
	public AbstractData getData(Coordinate coordinate) {
		// TODO Auto-generated method stub
		return null;
	}

}
