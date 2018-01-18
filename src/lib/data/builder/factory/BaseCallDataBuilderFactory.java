package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.BaseCallDataCache;
import lib.data.cache.DataCache;
import lib.data.has.hasBaseCallCount;
import lib.tmp.CoordinateController;

public class BaseCallDataBuilderFactory<T extends AbstractData & hasBaseCallCount> 
extends AbstractDataBuilderFactory<T> {

	public BaseCallDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) {

		final List<DataCache<T>> dataCaches = new ArrayList<DataCache<T>>(3);
		dataCaches.add(
				new BaseCallDataCache<T>(conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), 
						getParameter().getBaseConfig(), coordinateController));
		return dataCaches;
	}
	
}
