package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.DataCache;
import lib.data.cache.lrtarrest.LRTarrest2BaseCallCountDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class LRTarrestDataBuilderFactory<T extends AbstractData & HasCoverage & HasReferenceBase & HasBaseCallCount & HasLRTarrestCount> 
extends AbstractDataBuilderFactory<T> {

	public LRTarrestDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}

	@Override
	public List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<DataCache<T>> dataCaches = new ArrayList<DataCache<T>>(2);
		dataCaches.add(
				new LRTarrest2BaseCallCountDataCache<T>(
						conditionParameter.getLibraryType(),
						conditionParameter.getMinBASQ(),
						getParameter().getBaseConfig(),
						coordinateController));
		return dataCaches;
	}

}
