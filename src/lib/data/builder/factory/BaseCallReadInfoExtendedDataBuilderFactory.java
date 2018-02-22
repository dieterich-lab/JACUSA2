package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.DataCache;
import lib.data.cache.LinkageArrest2BaseChangeDataCache;
import lib.data.has.hasCoverage;
import lib.data.has.hasLinkedReadArrestCount;
import lib.data.has.hasReferenceBase;
import lib.tmp.CoordinateController;

public class BaseCallReadInfoExtendedDataBuilderFactory<T extends AbstractData & hasCoverage & hasReferenceBase & hasLinkedReadArrestCount> 
extends AbstractDataBuilderFactory<T> {

	public BaseCallReadInfoExtendedDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
	}

	@Override
	public List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<DataCache<T>> dataCaches = new ArrayList<DataCache<T>>(2);
		dataCaches.add(
				new LinkageArrest2BaseChangeDataCache<T>(
						conditionParameter.getLibraryType(), 
						getParameter().getBaseConfig(),
						conditionParameter.getMinBASQ(),
						coordinateController));
		return dataCaches;
	}

}
