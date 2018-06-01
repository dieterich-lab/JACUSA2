package lib.data.builder;

import java.util.Iterator;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.generator.DataGenerator;
import lib.data.has.HasLibraryType;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;

public class DataBuilder<T extends AbstractData>
implements HasLibraryType {

	private final int replicateIndex;
	
	private final DataGenerator<T> dataGenerator;
	private final AbstractConditionParameter<T> conditionParameter;

	private LIBRARY_TYPE libraryType;
	
	private final CacheContainer<T> cacheContainer; 
	private CACHE_STATUS cacheStatus;

	public DataBuilder(
			final int replicateIndex,
			final DataGenerator<T> dataGenerator, 
			final AbstractConditionParameter<T> conditionParameter,
			final LIBRARY_TYPE libraryType,
			final CacheContainer<T> cacheContainer) {
		
		this.replicateIndex = replicateIndex;
		
		this.dataGenerator = dataGenerator;
		this.conditionParameter	= conditionParameter;

		this.libraryType = libraryType;
		
		this.cacheContainer = cacheContainer;

		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public void buildCache(final Coordinate activeWindowCoordinate,
			final Iterator<SAMRecordWrapper> iterator) {
		
		clearCache();

		int records = 0;
		
		SAMRecordWrapper recordWrapper = null;
		try {
			while (iterator.hasNext()) {
				recordWrapper = iterator.next();
				recordWrapper.process();
				cacheContainer.add(recordWrapper);
				records++;
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
	public void clearCache() {
		cacheContainer.clear();
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public T getData(final Coordinate coordinate) {
		final T data = dataGenerator.createData(getLibraryType(), coordinate);
		cacheContainer.addData(data, coordinate);
		return data;
	}
	
	public CacheContainer<T> getCacheContainer() {
		return cacheContainer;
	}

	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameter;
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};

}
