package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.DataCache;
import lib.data.cache.PileupCountDataCache;
import lib.data.has.hasPileupCount;
import lib.tmp.CoordinateController;

public class PileupDataBuilderFactory<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilderFactory<T> {

	public PileupDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<T> conditionParameter) {

		final List<DataCache<T>> caches = new ArrayList<DataCache<T>>(1);
		caches.add(
				new PileupCountDataCache<T>(
						conditionParameter.getMaxDepth(), 
						conditionParameter.getMinBASQ(), 
						getParameter().getBaseConfig(), coordinateController));
		return caches;
	}
	
}
