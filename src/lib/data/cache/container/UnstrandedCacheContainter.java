package lib.data.cache.container;

import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.adder.DataContainerPopulator;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter 
implements CacheContainer {

	private final SharedCache sharedCache;
	private final List<RecordWrapperProcessor> processors;
	
	public UnstrandedCacheContainter(
			final SharedCache sharedCache, 
			final List<RecordWrapperProcessor> dataCaches) {

		this.sharedCache			= sharedCache;
		this.processors 				= dataCaches;
	}
	
	@Override
	public ReferenceProvider getReferenceProvider() {
		return sharedCache.getReferenceProvider();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return sharedCache.getNext(windowPosition);
	}
	
	@Override
	public void preProcess() {
		for (final RecordWrapperProcessor processor : processors) {
			processor.preProcess();
		}
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		sharedCache.addRecordWrapper(recordWrapper);

		for (final RecordWrapperProcessor processor : processors) {
			processor.process(recordWrapper);
		}
	}
	
	@Override
	public void postProcess() {
		for (final RecordWrapperProcessor processor : processors) {
			processor.postProcess();
		}
	}
	
	@Override
	public void populateContainer(DataTypeContainer container, Coordinate coordinate) {
		for (final DataContainerPopulator cache : processors) {
			cache.populate(container, coordinate);
		}
	}
	
	public void clear() {
		sharedCache.clear();
		for (final DataContainerPopulator dataCache : processors) {
			dataCache.clear();
		}
	}

	@Override
	public List<RecordWrapperProcessor> getCaches() {
		return processors;
	}

}
