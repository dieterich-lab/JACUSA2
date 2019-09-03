package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.storage.container.CacheContainer;
import lib.record.Record;
import lib.util.AbstractTool;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;

public class SiteDataAssembler implements DataAssembler {

	private final int replicateIndex;
	
	private final AbstractBuilderFactory builderFactory;
	private final ConditionParameter conditionParameter;

	private final CacheContainer cacheContainer; 
	private CACHE_STATUS cacheStatus;

	public SiteDataAssembler(
			final int replicateIndex,
			final AbstractBuilderFactory builderFactory, 
			final ConditionParameter conditionParameter,
			final CacheContainer cacheContainer) {
		
		this.replicateIndex 	= replicateIndex;
		
		this.builderFactory 	= builderFactory;
		this.conditionParameter	= conditionParameter;
		
		this.cacheContainer 	= cacheContainer;

		cacheStatus				= CACHE_STATUS.NOT_CACHED;
	}

	@Override
	public void buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<Record> iterator) {
		
		int records = 0;
		
		if (cacheStatus == CACHE_STATUS.CACHED) {
			clearStorage();
		}
		getCacheContainer().clearSharedStorage();
		
		Record record = null;
		try {
			if (iterator.hasNext()) {
				cacheContainer.preProcess();
				while (iterator.hasNext()) {
					record = iterator.next();
					cacheContainer.process(record);
					records++;
				}
				cacheContainer.postProcess();
			}
		} catch (Exception e){
			if (record != null) {
				AbstractTool.getLogger().addError("Problem with read: " + record.getSAMRecord().getReadName() + 
						" in " + conditionParameter.getRecordFilenames()[replicateIndex]);
			}
			e.printStackTrace();
			System.exit(1);
		}
		
		cacheStatus = records > 0 ? CACHE_STATUS.CACHED : CACHE_STATUS.NOT_FOUND; 
	}
	
	// Reset all caches in windows
	@Override
	public void clearStorage() {
		cacheContainer.clearStorage();
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	@Override
	public AbstractBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
	@Override
	public CacheContainer getCacheContainer() {
		return cacheContainer;
	}

	@Override
	public ConditionParameter getConditionParameter() {
		return conditionParameter;
	}

	@Override
	public LibraryType getLibraryType() {
		return conditionParameter.getLibraryType();
	}
	
	@Override
	public CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

}
