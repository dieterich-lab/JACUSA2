package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.PileupData;
import lib.data.adder.IncrementAdder;
import lib.data.adder.MapBaseCallQualityAdder;
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

public class PileupDataBuilderFactory 
extends AbstractDataBuilderFactory<PileupData> {

	public PileupDataBuilderFactory(final AbstractParameter<PileupData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordWrapperDataCache<PileupData>> createCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<PileupData> conditionParameter) {

		final List<IncrementAdder<PileupData>> adder = new ArrayList<IncrementAdder<PileupData>>();
		final BaseCallAdder<PileupData> baseCallAdder = 
				new ArrayBaseCallAdder<PileupData>(new DefaultBaseCallCountExtractor<PileupData>(), coordinateController);
		adder.add(baseCallAdder);
		final IncrementAdder<PileupData> baseCallQualityAdder = 
				new MapBaseCallQualityAdder<PileupData>(coordinateController);
		adder.add(baseCallQualityAdder);

		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), baseCallAdder));
		}

		final ValidatedRegionDataCache<PileupData> regionDataCache = 
				new ValidatedRegionDataCache<PileupData>(adder, validator, coordinateController);
		
		final List<RecordWrapperDataCache<PileupData>> dataCaches = new ArrayList<RecordWrapperDataCache<PileupData>>(3);
		dataCaches.add(new AlignmentBlockWrapperDataCache<PileupData>(regionDataCache));
		return dataCaches;
	}
	
}
