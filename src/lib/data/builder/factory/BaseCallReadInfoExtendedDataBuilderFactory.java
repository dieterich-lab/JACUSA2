package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.DataCache;
import lib.data.cache.LRTarrest2BaseChangeDataCache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasCoverage;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class BaseCallReadInfoExtendedDataBuilderFactory<T extends AbstractData & hasCoverage & hasReferenceBase & hasBaseCallCount & hasLRTarrestCount> 
extends AbstractDataBuilderFactory<T> {

	public BaseCallReadInfoExtendedDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}

	@Override
	public List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<DataCache<T>> dataCaches = new ArrayList<DataCache<T>>(2);
		dataCaches.add(
				new LRTarrest2BaseChangeDataCache<T>(
						conditionParameter.getLibraryType(),
						getParameter().getBaseConfig(),
						coordinateController));
		return dataCaches;
	}

}
