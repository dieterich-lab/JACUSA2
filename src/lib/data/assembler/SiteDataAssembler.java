package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.storage.container.CacheContainer;
import lib.util.AbstractTool;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;

public class SiteDataAssembler
implements DataAssembler {

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
			final Iterator<SAMRecordExtended> iterator) {

		int records = 0;

		if (cacheStatus == CACHE_STATUS.CACHED) {
			clearStorage();
		}
		getCacheContainer().clearSharedStorage();
		
		SAMRecordExtended recordExtended = null;
		try {
			if (iterator.hasNext()) {
				cacheContainer.preProcess();
				while (iterator.hasNext()) {
					recordExtended = iterator.next();
					cacheContainer.process(recordExtended);
					records++;
				}
				cacheContainer.postProcess();
			}
		} catch (Exception e){
			if (recordExtended != null) {
				AbstractTool.getLogger().addError("Problem with read: " + recordExtended.getSAMRecord().getReadName() + 
						" in " + conditionParameter.getRecordFilenames()[replicateIndex]);
			}
			e.printStackTrace();
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
