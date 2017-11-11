package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.util.Coordinate;

public class RTArrestPileupBuilder<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractDataBuilder<T> {

	private final AbstractDataBuilder<T> dataBuilder;

	public RTArrestPileupBuilder(final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T> generalParameter, final AbstractDataBuilder<T> dataBuilder, 
			final Cache<T> cache) {
		super(conditionParameter, generalParameter, dataBuilder.getLibraryType(), cache);
		this.dataBuilder = dataBuilder;
	}
		
	@Override
	public T getData(final Coordinate coordinate) {
		T data = dataBuilder.getData(coordinate);

		/*
		// TODO should bed
		data.getReadInfoCount().setStart(cache.getReadStartCount(coordinate));
		data.getReadInfoCount().setEnd(cache.getReadEndCount(coordinate));
		
		final int inner = cache.getCoverage(windowPosition, strand) - 
				//		readStartCount[windowPosition] - 
				//		readEndCount[windowPosition]
		// TODO data.getReadInfoCount().setInner();
		 * 
		 */

		int arrest = 0;
		int through = 0;

		switch (getLibraryType()) {
		
		case UNSTRANDED:
			arrest 	+= data.getReadInfoCount().getStart();
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_SECONDSTRAND:
			arrest 	+= data.getReadInfoCount().getStart();
			through += data.getReadInfoCount().getInner();
			break;				
		}

		data.getReadInfoCount().setArrest(arrest);
		data.getReadInfoCount().setThrough(through);

		return data;
	}

	@Override
	public void clearCache() {
		dataBuilder.clearCache();
		super.clearCache();
	}

}