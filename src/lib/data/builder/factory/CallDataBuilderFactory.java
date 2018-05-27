package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.CallData;
import lib.data.cache.PileupDataCache;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordDataCache;
import lib.util.coordinate.CoordinateController;

public class CallDataBuilderFactory 
extends AbstractDataBuilderFactory<CallData> {

	public CallDataBuilderFactory(final AbstractParameter<CallData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordDataCache<CallData>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<CallData> conditionParameter) {

		final List<RecordDataCache<CallData>> caches = new ArrayList<RecordDataCache<CallData>>(1);
		caches.add(
				new AlignmentBlockWrapperDataCache<CallData>(
						new PileupDataCache<CallData>(
								new DefaultBaseCallCountExtractor<CallData>(),
									conditionParameter.getMaxDepth(), 
									conditionParameter.getMinBASQ(),
									coordinateController)));
		return caches;
	}
	
}
