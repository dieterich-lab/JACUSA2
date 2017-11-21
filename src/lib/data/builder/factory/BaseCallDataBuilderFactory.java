package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.BaseCallCache;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;
import lib.tmp.CoordinateController;

public class BaseCallDataBuilderFactory<T extends AbstractData & hasBaseCallCount> 
extends AbstractDataBuilderFactory<T> {

	public BaseCallDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<Cache<T>> createCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) {

		final List<Cache<T>> caches = new ArrayList<Cache<T>>(3);
		caches.add(
				new BaseCallCache<T>(
						conditionParameter.getMaxDepth(), 
						conditionParameter.getMinBASQ(), 
						getParameter().getBaseConfig(), coordinateController));
		return caches;
	}
	
}
