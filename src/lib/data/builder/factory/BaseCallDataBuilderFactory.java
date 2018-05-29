package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.BaseCallData;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.basecall.BaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.util.coordinate.CoordinateController;

public class BaseCallDataBuilderFactory
extends AbstractDataBuilderFactory<BaseCallData> {

	public BaseCallDataBuilderFactory(final AbstractParameter<BaseCallData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordWrapperDataCache<BaseCallData>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<BaseCallData> conditionParameter) {

		final List<IncrementAdder<BaseCallData>> adder = new ArrayList<IncrementAdder<BaseCallData>>();
		final BaseCallAdder<BaseCallData> baseCallAdder = 
				new ArrayBaseCallAdder<BaseCallData>(new DefaultBaseCallCountExtractor<BaseCallData>(), coordinateController);
		adder.add(baseCallAdder);
		
		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), baseCallAdder));
		}

		final ValidatedRegionDataCache<BaseCallData> regionDataCache = 
				new ValidatedRegionDataCache<BaseCallData>(adder, validator, coordinateController);
		
		final List<RecordWrapperDataCache<BaseCallData>> dataCaches = new ArrayList<RecordWrapperDataCache<BaseCallData>>(3);
		dataCaches.add(new AlignmentBlockWrapperDataCache<BaseCallData>(regionDataCache));
		return dataCaches;
	}
	
}
