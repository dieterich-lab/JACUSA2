package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.rtarrest.RTarrestMethod.RTarrestBuilderFactory;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.arrest.RTarrestRecordProcessor;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.DefaultBCCStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.data.validator.WindowPositionValidator;
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
		
		final List<Validator> validators = new ArrayList<>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		
		cache.addCache(createDefault(
				sharedStorage, 
				locInterpreter, 
				validators));

		if (parameter.showInsertionCount() || parameter.showInsertionStartCount() || parameter.showDeletionCount()) {
			cache.addCache(createCoverageCache(sharedStorage, DataType.COVERAGE.getFetcher()));
		}
		addInsertionCache(parameter, sharedStorage, cache);
		addDeletionCache(parameter, sharedStorage, cache);

		return cache;
	}

	private Cache createDefault(
			final SharedStorage sharedStorage,
			final LocationInterpreter locInterpreter, 
			final List<Validator> validators) {
		
		Cache cache = new Cache();
		
		final AbstractBaseCallCountStorage arrestBccStorage = new DefaultBCCStorage(
					sharedStorage,
					DataType.ARREST_BCC.getFetcher());
		cache.addStorage(arrestBccStorage);
		
		final AbstractBaseCallCountStorage throughBccStorage = new DefaultBCCStorage(
				sharedStorage,
				DataType.THROUGH_BCC.getFetcher());
		cache.addStorage(throughBccStorage);
		
		final WindowPositionValidator winPosValidator = new WindowPositionValidator();
		final PositionProcessor arrestPositionProcessor = 
				new PositionProcessor(validators, arrestBccStorage);
		arrestPositionProcessor.addValidator(winPosValidator);
		
		final PositionProcessor throughPositionProcessor = 
				new PositionProcessor(validators, throughBccStorage);		
		throughPositionProcessor.addValidator(winPosValidator);
		
		cache.addRecordProcessor(new RTarrestRecordProcessor(
				sharedStorage,
				locInterpreter, 
				arrestPositionProcessor, throughPositionProcessor));
				
		return cache;
	}
	
}
