package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.BaseCallData;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordDataCache;
import lib.data.cache.region.ArrayBaseCallRegionDataCache;
import lib.util.coordinate.CoordinateController;

public class BaseCallDataBuilderFactory
extends AbstractDataBuilderFactory<BaseCallData> {

	public BaseCallDataBuilderFactory(final AbstractParameter<BaseCallData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordDataCache<BaseCallData>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<BaseCallData> conditionParameter) {

		final List<RecordDataCache<BaseCallData>> dataCaches = new ArrayList<RecordDataCache<BaseCallData>>(3);
		dataCaches.add(
				new AlignmentBlockWrapperDataCache<BaseCallData>(
						new ArrayBaseCallRegionDataCache<BaseCallData>(
								new DefaultBaseCallCountExtractor<BaseCallData>(),
								conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(),
								getParameter().getBaseConfig(), coordinateController)));
		return dataCaches;
	}
	
}
