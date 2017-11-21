package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.Cache;
import lib.data.cache.PileupCountCache;
import lib.data.has.hasPileupCount;
import lib.tmp.CoordinateController;

public class PileupDataBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilderFactory<T> {

	public PileupDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<Cache<T>> createCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) {

		final List<Cache<T>> caches = new ArrayList<Cache<T>>(1);
		caches.add(
				new PileupCountCache<T>(
						conditionParameter.getMaxDepth(), 
						conditionParameter.getMinBASQ(), 
						getParameter().getBaseConfig(), coordinateController));
		return caches;
	}
	
}
