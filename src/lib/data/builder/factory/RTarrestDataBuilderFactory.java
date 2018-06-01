package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.RTarrestData;
import lib.data.cache.ArrestThroughDataCache;
import lib.data.cache.extractor.basecall.ArrestBaseCallCountExtractor;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.extractor.basecall.DefaultRTcountExtractor;
import lib.data.cache.extractor.basecall.ThroughBaseCallCountExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.CoordinateController;

public class RTarrestDataBuilderFactory 
extends AbstractDataBuilderFactory<RTarrestData> {

	public RTarrestDataBuilderFactory(final AbstractParameter<RTarrestData, ?> generalParameter) {
		super(generalParameter);
	}

	@Override
	public List<RecordWrapperDataCache<RTarrestData>> createCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<RTarrestData> conditionParameter) {

		final List<RecordWrapperDataCache<RTarrestData>> dataCaches = new ArrayList<RecordWrapperDataCache<RTarrestData>>(3);
		dataCaches.add(
				new ArrestThroughDataCache<RTarrestData>(
						new DefaultRTcountExtractor<RTarrestData>(),
						new DefaultBaseCallCountExtractor<RTarrestData>(),
						new ArrestBaseCallCountExtractor<RTarrestData>(),
						new ThroughBaseCallCountExtractor<RTarrestData>(),
						conditionParameter.getLibraryType(), 
						conditionParameter.getMinBASQ(), 
						coordinateController));
		return dataCaches;
	}

}
