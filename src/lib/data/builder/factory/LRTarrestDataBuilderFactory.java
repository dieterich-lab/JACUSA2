package lib.data.builder.factory;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.LRTarrestData;
import lib.data.cache.record.RecordDataCache;
import lib.util.coordinate.CoordinateController;

// FIXME
public class LRTarrestDataBuilderFactory 
extends AbstractDataBuilderFactory<LRTarrestData> {

	public LRTarrestDataBuilderFactory(final AbstractParameter<LRTarrestData, ?> generalParameter) {
		super(generalParameter);
	}

	@Override
	public List<RecordDataCache<LRTarrestData>> createDataCaches(
			final CoordinateController coordinateController, 
			final AbstractConditionParameter<LRTarrestData> conditionParameter) {
		
		/*
		final List<RecordDataCache<LRTarrestData>> dataCaches = new ArrayList<RecordDataCache<LRTarrestData>>(2);
		dataCaches.add(
				new LRTarrest2BaseCallCountDataCache<LRTarrestData>(
						new DefaultBaseCallCountExtractor<LRTarrestData>(),
						null,
						new DefaultLRTarrestCountExtractor<LRTarrestData>(),
						conditionParameter.getLibraryType(),
						conditionParameter.getMinBASQ(),
						getParameter().getBaseConfig(),
						coordinateController));
		return dataCaches;
		*/
		return null; // FIXME
	}

}
