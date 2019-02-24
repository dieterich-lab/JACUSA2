package lib.data.storage.container;

import java.util.ArrayList;
import java.util.List;

import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;
import lib.data.DataContainer;
import lib.data.storage.Storage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;

public abstract class AbstractStrandedCacheContainer 
implements CacheContainer {

	private final CacheContainer forwardContainer; 
	private final CacheContainer reverseContainer;

	private final List<RecordExtendedPrePostProcessor> recordProcessors;
	private final List<Storage> storages;
	
	public AbstractStrandedCacheContainer(
			final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {

		this.forwardContainer	= forwardContainer;
		this.reverseContainer 	= reverseContainer;
		
		recordProcessors = new ArrayList<>();
		recordProcessors.addAll(forwardContainer.getRecordProcessors());
		recordProcessors.addAll(reverseContainer.getRecordProcessors());
		
		storages = new ArrayList<>();
		storages.addAll(forwardContainer.getStorages());
		storages.addAll(reverseContainer.getStorages());
	}
	
	@Override
	public ReferenceProvider getReferenceProvider() {
		return forwardContainer.getReferenceProvider();
	}
	
	@Override
	public int getNextWindowPosition(int winPos) {
		final int forwardNext = forwardContainer.getNextWindowPosition(winPos);
		final int reverseNext = reverseContainer.getNextWindowPosition(winPos);

		int nextWinPos = -1;
		if (forwardNext == -1 || reverseNext == -1) {
			nextWinPos = Math.max(forwardNext, reverseNext);
		} else {
			nextWinPos = Math.min(forwardNext, reverseNext);
		}

		return nextWinPos;
	}
	
	@Override
	public List<RecordExtendedPrePostProcessor> getRecordProcessors() {
		return recordProcessors;
	}

	@Override
	public List<Storage> getStorages() {
		return storages;
	}
	
	@Override
	public void clearSharedStorage() {
		forwardContainer.clearSharedStorage();
	}
	
	@Override
	public void preProcess() {
		forwardContainer.preProcess();
		reverseContainer.preProcess();
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		getCacheContainer(recordExtended).process(recordExtended);
	}
	
	@Override
	public void postProcess() {
		forwardContainer.postProcess();
		reverseContainer.postProcess();
	}
	
	@Override
	public void populate(DataContainer container, Coordinate coordinate) {
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
	
	protected abstract CacheContainer getCacheContainer(final SAMRecordExtended recordExtended);
	
	public CacheContainer getForwardContainer() {
		return forwardContainer;
	}

	public CacheContainer getReverseContainer() {
		return reverseContainer;
	}
	
}
