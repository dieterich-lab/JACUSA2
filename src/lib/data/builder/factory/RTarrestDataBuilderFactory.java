package lib.data.builder.factory;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.AlignmentDataCache;
import lib.data.cache.DataCache;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.util.coordinate.CoordinateController;

public class RTarrestDataBuilderFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasArrestBaseCallCount & HasThroughBaseCallCount & HasRTarrestCount> 
extends AbstractDataBuilderFactory<T> {

	private AbstractDataBuilderFactory<T> dataBuilderFactory; 
	
	public RTarrestDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
		dataBuilderFactory = new BaseCallDataBuilderFactory<T>(generalParameter);
	}

	@Override
	public List<DataCache<T>> createDataCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<DataCache<T>> dataCaches = dataBuilderFactory.createDataCaches(coordinateController, conditionParameter);
		dataCaches.add(
				new AlignmentDataCache<T>(
						conditionParameter.getLibraryType(), 
						conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), 
						getParameter().getBaseConfig(), coordinateController));
		return dataCaches;
	}

}
