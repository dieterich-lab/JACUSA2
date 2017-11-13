package lib.data.builder;

import java.util.Iterator;
import java.util.List;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.util.Coordinate;

public class RTArrestPileupBuilder<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractDataBuilder<T> {

	private final AbstractDataBuilder<T> dataBuilder;

	public RTArrestPileupBuilder(final AbstractConditionParameter<T> conditionParameter,
			final AbstractDataBuilder<T> dataBuilder,
			final Cache<T> cache,
			final FilterContainer<T> filterContainer) {
		super(conditionParameter, dataBuilder.getLibraryType(), cache, filterContainer);
		this.dataBuilder = dataBuilder;
	}

	@Override
	public List<SAMRecordWrapper> buildCache(Coordinate activeWindowCoordinate,
			Iterator<SAMRecordWrapper> iterator) {
		// TODO Auto-generated method stub
		return super.buildCache(activeWindowCoordinate, iterator);
	}
	
	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getCache().getData(coordinate);

		final int inner = data.getCoverage() -
				(data.getReadInfoCount().getStart() + data.getReadInfoCount().getEnd());
		data.getReadInfoCount().setInner(inner);

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