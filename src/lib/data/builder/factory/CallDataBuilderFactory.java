package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.CallData;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.basecall.BaseCallAdder;
import lib.data.adder.basecall.MapBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.util.coordinate.CoordinateController;

public class CallDataBuilderFactory 
extends AbstractDataBuilderFactory<CallData> {

	public CallDataBuilderFactory(final AbstractParameter<CallData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordWrapperDataCache<CallData>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<CallData> conditionParameter) {

		final BaseCallCountExtractor<CallData> baseCallCountExtractor = new DefaultBaseCallCountExtractor<CallData>();
		
		final List<IncrementAdder<CallData>> adder = new ArrayList<IncrementAdder<CallData>>();
		final BaseCallAdder<CallData> baseCallAdder = 
				new ArrayBaseCallAdder<CallData>(new DefaultBaseCallCountExtractor<CallData>(), coordinateController);
		adder.add(baseCallAdder);
		final IncrementAdder<CallData> baseCallQualityAdder = 
				new MapBaseCallAdder<CallData>(baseCallCountExtractor, coordinateController);
		adder.add(baseCallQualityAdder);

		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), baseCallAdder));
		}

		final ValidatedRegionDataCache<CallData> regionDataCache = 
				new ValidatedRegionDataCache<CallData>(adder, validator, coordinateController);
		
		final List<RecordWrapperDataCache<CallData>> dataCaches = new ArrayList<RecordWrapperDataCache<CallData>>(3);
		dataCaches.add(new AlignmentBlockWrapperDataCache<CallData>(regionDataCache));
		return dataCaches;
	}
	
}
