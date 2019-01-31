package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.method.rtarrest.RTarrestMethod.RTarrestBuilderFactory;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.fetcher.basecall.BaseCallCountExtractor;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.arrest.RTarrestRecordProcessor;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.DefaultBaseCallCountStorage;
import lib.data.storage.basecall.RTarrestCountStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubstitutionRecordProcessor;
import lib.data.stroage.Storage;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.LibraryType;

public class RTarrestDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	public RTarrestDataAssemblerFactory(final RTarrestBuilderFactory builderFactory) {
		super(builderFactory);
	}
	
	@Override
	public Cache createCache(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {

		final Cache cache = new Cache();

		final LibraryType libraryType = conditionParameter.getLibraryType();

		final LocationInterpreter locInterpreter = LocationInterpreter.create(libraryType);
		final SortedSet<BaseSubstitution> baseSubs = parameter.getReadSubstitutions();
		
		final List<Validator> validators = new ArrayList<Validator>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		
		cache.addCache(createDefault(
				sharedStorage, 
				locInterpreter, 
				validators));

		// stratify by base substitutions
		if (baseSubs.size() > 0) {
			cache.addCache(createStratifyByBaseSubstitution(
							baseSubs,
							sharedStorage, 
							locInterpreter,
							BaseCallInterpreter.build(libraryType),
							validators));
		}

		return cache;
	}

	private Cache createDefault(
			final SharedStorage sharedStorage,
			final LocationInterpreter locInterpreter, 
			final List<Validator> validators) {
		
		Cache cache = new Cache();
		
		final AbstractBaseCallCountStorage arrestBccStorage = new DefaultBaseCallCountStorage(
					sharedStorage,
					DataType.ARREST_BCC.getFetcher());
		cache.addStorage(arrestBccStorage);
		
		final AbstractBaseCallCountStorage throughBccStorage = new DefaultBaseCallCountStorage(
				sharedStorage,
				DataType.THROUGH_BCC.getFetcher());
		cache.addStorage(throughBccStorage);
		
		final PositionProcessor arrestPositionProcessor = 
				new PositionProcessor(validators, arrestBccStorage);
		final PositionProcessor throughPositionProcessor = 
				new PositionProcessor(validators, throughBccStorage);		
		
		cache.addRecordProcessor(new RTarrestRecordProcessor(
				sharedStorage,
				locInterpreter, 
				arrestPositionProcessor, throughPositionProcessor));
		
		return cache;
	}
	
	private Cache createStratifyByBaseSubstitution(
			final SortedSet<BaseSubstitution> baseSubs,
			final SharedStorage sharedStorage,
			final LocationInterpreter locInterpreter,
			final BaseCallInterpreter baseCallInterpreter,
			final List<Validator> validators) {
				
		final Cache cache = new Cache();
		final Map<BaseSubstitution, Storage> basSub2storage = new HashMap<>(baseSubs.size());
		for (final BaseSubstitution baseSub : baseSubs) {
			
			final AbstractBaseCallCountStorage arrestBccStorage = new DefaultBaseCallCountStorage(
					sharedStorage,
					new BaseCallCountExtractor(
							baseSub, 
							DataType.ARREST_BASE_SUBST.getFetcher()));
			
			final AbstractBaseCallCountStorage throughBccStorage = new DefaultBaseCallCountStorage(
					sharedStorage,
					new BaseCallCountExtractor(
							baseSub, 
							DataType.THROUGH_BASE_SUBST.getFetcher()));
			
			final Storage rtArrestCountStorage = new RTarrestCountStorage(
							sharedStorage,
							locInterpreter,
							arrestBccStorage, throughBccStorage);
			cache.addStorage(rtArrestCountStorage);
			basSub2storage.put(baseSub, rtArrestCountStorage);
		}	

		cache.addRecordProcessor(new BaseSubstitutionRecordProcessor(
				sharedStorage, 
				baseCallInterpreter, 
				new CombinedValidator(validators), 
				basSub2storage));
		
		return cache;
	}
	
}
