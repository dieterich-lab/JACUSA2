package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.call.CallMethod.CallBuilderFactory;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.MapBaseCallQualityAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.count.PileupCount;

public class CallDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	private final Fetcher<PileupCount> pcFetcher;
	
	public CallDataAssemblerFactory(final CallBuilderFactory builderFactory) {
		super(builderFactory);
		pcFetcher = DataType.PILEUP_COUNT.getFetcher();
	}
	
	protected List<RecordWrapperProcessor> createCaches(
			final GeneralParameter parameter,
			final SharedCache sharedCache, 
			final ConditionParameter conditionParameter) {

		final List<IncrementAdder> adder = new ArrayList<IncrementAdder>();

		final IncrementAdder bcqAdder = 
				new MapBaseCallQualityAdder(sharedCache, pcFetcher);
		adder.add(bcqAdder);

		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		validator.add(new DefaultBaseCallValidator());
		final MinBASQBaseCallValidator minBASQbcValidator = 
				new MinBASQBaseCallValidator(conditionParameter.getMinBASQ());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(minBASQbcValidator);
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), bcqAdder));
		}

		final ValidatedRegionDataCache regionDataCache = 
				new ValidatedRegionDataCache(adder, validator, sharedCache);
		
		final List<RecordWrapperProcessor> dataCaches = new ArrayList<RecordWrapperProcessor>(3);
		dataCaches.add(new AlignmentBlockWrapperDataCache(regionDataCache));
		
		addBaseSubstitution(
				parameter,
				sharedCache,
				conditionParameter, 
				dataCaches);

		return dataCaches;
	}
	
}
