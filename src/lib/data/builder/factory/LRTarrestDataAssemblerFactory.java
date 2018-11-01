package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.lrtarrest.LRTarrestMethod.LRTarrestBuilderFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.DataType;
import lib.data.adder.IncrementAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.cache.lrtarrest.EndLRTarrestBaseCallAdder;
import lib.data.cache.lrtarrest.StartLRTarrestBaseCallAdder;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;

public class LRTarrestDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	public LRTarrestDataAssemblerFactory(final LRTarrestBuilderFactory builderFactory) {
		super(builderFactory);
	}

	@Override
	public List<RecordWrapperDataCache> createCaches(
			final AbstractParameter parameter,
			final SharedCache sharedCache, 
			final AbstractConditionParameter conditionParameter) {
		
		final LibraryType libraryType = conditionParameter.getLibraryType();

		final Fetcher<ArrestPos2BaseCallCount> arrestPos2BaseCallCountFetcher =
				DataType.AP2BCC.getFetcher();

		IncrementAdder adder = null;
		
		switch (libraryType) {
		
		case UNSTRANDED:
			adder = new EndLRTarrestBaseCallAdder(arrestPos2BaseCallCountFetcher, sharedCache);
			break;

		case RF_FIRSTSTRAND:
			adder = new EndLRTarrestBaseCallAdder(arrestPos2BaseCallCountFetcher, sharedCache);
			break;

		case FR_SECONDSTRAND:
			adder = new StartLRTarrestBaseCallAdder(arrestPos2BaseCallCountFetcher, sharedCache);
			break;
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
		
		final List<IncrementAdder> adders = new ArrayList<IncrementAdder>();
		adders.add(adder);
		
		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}

		final ValidatedRegionDataCache regionDataCache = 
				new ValidatedRegionDataCache(adders, validator, sharedCache);
		
		final List<RecordWrapperDataCache> dataCaches = new ArrayList<RecordWrapperDataCache>(3);
		dataCaches.add(new AlignmentBlockWrapperDataCache(regionDataCache));
		return dataCaches;
	}

}
