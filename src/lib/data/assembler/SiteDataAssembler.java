package lib.data.assembler;

import java.util.Iterator;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.has.LibraryType;
import lib.util.AbstractTool;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

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
		
		this.replicateIndex = replicateIndex;
		
		this.builderFactory = builderFactory;
		this.conditionParameter	= conditionParameter;
		
		this.cacheContainer = cacheContainer;

		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	@Override
	public void buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<SAMRecordWrapper> iterator) {
		
		clearCache();

		int records = 0;
		
		SAMRecordWrapper recordWrapper = null;
		try {
			if (iterator.hasNext()) {
				cacheContainer.preProcess();
				while (iterator.hasNext()) {
					recordWrapper = iterator.next();
					cacheContainer.process(recordWrapper);
					records++;
				}
				cacheContainer.postProcess();
			}
		} catch (Exception e){
			if (recordWrapper != null) {
				AbstractTool.getLogger().addError("Problem with read: " + recordWrapper.getSAMRecord().getReadName() + 
						" in " + conditionParameter.getRecordFilenames()[replicateIndex]);
			}
			e.printStackTrace();
		}

		cacheStatus = records > 0 ? CACHE_STATUS.CACHED : CACHE_STATUS.NOT_FOUND; 
	}
	
	// Reset all caches in windows
	@Override
	public void clearCache() {
		cacheContainer.clear();
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	@Override
	public DataTypeContainer assembleData(final Coordinate coordinate) {
		final Base referenceBase = getCacheContainer().getReferenceProvider().getReferenceBase(coordinate);
		final DataTypeContainer container =
				builderFactory.createBuilder(coordinate, getLibraryType())
					.withReferenceBase(referenceBase)
					.build();
		cacheContainer.populateContainer(container, coordinate);
		return container;
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
