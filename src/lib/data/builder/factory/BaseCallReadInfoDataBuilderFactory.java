package lib.data.builder.factory;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.AlignmentDataCache;
import lib.data.cache.DataCache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.tmp.CoordinateController;

public class BaseCallReadInfoDataBuilderFactory<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractDataBuilderFactory<T> {

	private AbstractDataBuilderFactory<T> dataBuilderFactory; 
	
	public BaseCallReadInfoDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
		dataBuilderFactory = new BaseCallDataBuilderFactory<T>(generalParameter);
	}

	@Override
	public List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<DataCache<T>> dataCaches = dataBuilderFactory.createDataCaches(coordinateController, conditionParameter);
		dataCaches.add(
				new AlignmentDataCache<T>(
						conditionParameter.getLibraryType(), 
						coordinateController));
		return dataCaches;
	}

}
