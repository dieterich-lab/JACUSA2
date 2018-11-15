package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.method.rtarrest.RTarrestMethod.RTarrestBuilderFactory;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.DataType;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.DefaultBaseCallAdder;
import lib.data.adder.basecall.RTarrestBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.arrest.LocationInterpreter;
import lib.data.cache.arrest.RTarrestDataCache;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.basecall.BaseCallCountExtractor;
import lib.data.cache.readsubstitution.BaseCallInterpreter;
import lib.data.cache.readsubstitution.ReadSubstitutionCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.IntegrateValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;

public class RTarrestDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	public RTarrestDataAssemblerFactory(final RTarrestBuilderFactory builderFactory) {
		super(builderFactory);
	}
	
	@Override
	public List<RecordWrapperDataCache> createCaches(
			final AbstractParameter parameter,
			final SharedCache sharedCache, 
			final AbstractConditionParameter conditionParameter) {

		final List<RecordWrapperDataCache> dataCaches = new ArrayList<>(3);

		final byte minBASQ = conditionParameter.getMinBASQ();
		final LibraryType libraryType = conditionParameter.getLibraryType();

		final LocationInterpreter locInterpreter = LocationInterpreter.create(libraryType);
		final SortedSet<BaseSubstitution> baseSubs = parameter.getReadSubstitutions();
		
		// TODO what other validators
		final List<BaseCallValidator> validators = new ArrayList<BaseCallValidator>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQBaseCallValidator(minBASQ));
		}
		
		dataCaches.add(
				createDefault(
						sharedCache, 
						locInterpreter, 
						validators));

		// stratify by base substitutions
		if (baseSubs.size() > 0) {
			dataCaches.add(
					createStratifyByBaseSubstitution(
							baseSubs,
							sharedCache, 
							locInterpreter,
							BaseCallInterpreter.create(libraryType),
							validators));
		}

		return dataCaches;
	}

	private RecordWrapperDataCache createDefault(
			final SharedCache sharedCache,
			final LocationInterpreter locInterpreter, 
			final List<BaseCallValidator> validators) {
		
		final ValidatedRegionDataCache arrest = new ValidatedRegionDataCache(sharedCache);
		arrest.addAdder(
				new DefaultBaseCallAdder(
					sharedCache,
					DataType.ARREST_BCC.getFetcher()));
		
		final ValidatedRegionDataCache through = new ValidatedRegionDataCache(sharedCache);
		through.addAdder(
				new DefaultBaseCallAdder(
						sharedCache,
						DataType.THROUGH_BCC.getFetcher()));
		for (final BaseCallValidator validator : validators) {
			arrest.addValidator(validator);
			through.addValidator(validator);
		}
		return new RTarrestDataCache(locInterpreter, arrest, through, sharedCache);
	}
	
	private RecordWrapperDataCache createStratifyByBaseSubstitution(
			final SortedSet<BaseSubstitution> baseSubs,
			final SharedCache sharedCache,
			final LocationInterpreter locationInterpreter,
			final BaseCallInterpreter baseCallInterpreter,
			final List<BaseCallValidator> validators) {
				
		final Map<BaseSubstitution, IncrementAdder> sub2adder = new HashMap<>(baseSubs.size());
		for (final BaseSubstitution baseSub : baseSubs) {
			final IncrementAdder arrest = new DefaultBaseCallAdder(
					sharedCache,
					new BaseCallCountExtractor(
							baseSub, 
							DataType.ARREST_BASE_SUBST.getFetcher()));
			final IncrementAdder through = new DefaultBaseCallAdder(
					sharedCache,
					new BaseCallCountExtractor(
							baseSub, 
							DataType.THROUGH_BASE_SUBST.getFetcher()));			
			final IncrementAdder adder = 
					new RTarrestBaseCallAdder(
							sharedCache,
							locationInterpreter,
							arrest,
							through);
			sub2adder.put(baseSub, adder);
		}	
		return new ReadSubstitutionCache(
				sharedCache, 
				baseCallInterpreter, 
				new IntegrateValidator(validators), 
				sub2adder); 
	}
	
}
