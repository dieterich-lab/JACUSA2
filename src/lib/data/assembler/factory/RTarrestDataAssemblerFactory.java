package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.method.rtarrest.RTarrestDataContainerBuilderFactory;
import lib.cli.options.filter.has.BaseSub;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.fetcher.ExtractBaseSub;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.arrest.RTarrestRecordProcessor;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.DefaultBCCStorage;
import lib.data.storage.basecall.RTarrestCountStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.integer.ArrayIntegerStorage;
import lib.data.storage.integer.MapIntegerStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.data.validator.WindowPositionValidator;
import lib.util.LibraryType;

public class RTarrestDataAssemblerFactory extends AbstractDataAssemblerFactory<RTarrestDataContainerBuilderFactory> {

	public RTarrestDataAssemblerFactory(final RTarrestDataContainerBuilderFactory builderFactory) {
		super(builderFactory);
	}

	@Override
	public Cache createCache(final GeneralParameter parameter, final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter) {

		final Cache cache = new Cache();

		final LibraryType libraryType = conditionParameter.getLibraryType();

		final LocationInterpreter locInterpreter = LocationInterpreter.create(libraryType);
		final SortedSet<BaseSub> baseSubs = parameter.getReadTags();

		final List<Validator> validators = new ArrayList<>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}

		cache.addCache(createDefault(sharedStorage, locInterpreter, validators));

		if (parameter.showInsertionCount() || parameter.showDeletionCount()) {
			cache.addCache(Cache.createReadCoverageCache(sharedStorage, getDataContainerBuilderFactory().readsDt));
		}
		if (parameter.showInsertionCount()) {
			cache.addCache(Cache.createInsertionCache(sharedStorage, getDataContainerBuilderFactory().insertionsDt));
		}
		if (parameter.showDeletionCount()) {
			cache.addCache(Cache.createDeletionCache(sharedStorage, getDataContainerBuilderFactory().deletionsDt));
		}

		// stratify by base substitutions
		if (!baseSubs.isEmpty()) {
			cache.addCache(createStratifyByBaseSub(parameter, conditionParameter, baseSubs, sharedStorage,
					locInterpreter, validators));
		}

		return cache;
	}

	private Cache createDefault(final SharedStorage sharedStorage, final LocationInterpreter locInterpreter,
			final List<Validator> validators) {

		Cache cache = new Cache();

		final AbstractBaseCallCountStorage arrestBccStorage = new DefaultBCCStorage(sharedStorage,
				getDataContainerBuilderFactory().arrestBccDt);
		cache.addStorage(arrestBccStorage);

		final AbstractBaseCallCountStorage throughBccStorage = new DefaultBCCStorage(sharedStorage,
				getDataContainerBuilderFactory().throughBccDt);
		cache.addStorage(throughBccStorage);

		final WindowPositionValidator winPosValidator = new WindowPositionValidator();
		final PositionProcessor arrestPositionProcessor = new PositionProcessor(validators, arrestBccStorage);
		arrestPositionProcessor.addValidator(winPosValidator);

		final PositionProcessor throughPositionProcessor = new PositionProcessor(validators, throughBccStorage);
		throughPositionProcessor.addValidator(winPosValidator);

		cache.addRecordProcessor(new RTarrestRecordProcessor(sharedStorage, locInterpreter, arrestPositionProcessor,
				throughPositionProcessor));

		return cache;
	}

	private Cache createStratifyByBaseSub(final GeneralParameter parameter, final ConditionParameter conditionParameter,
			final SortedSet<BaseSub> baseSubs, final SharedStorage sharedStorage,
			final LocationInterpreter locInterpreter, final List<Validator> validators) {

		final Cache cache = new Cache();

		final Map<BaseSub, PositionProcessor> baseSub2alignedPosProcessor = new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2covPosProcessor = new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2insPosProcessor = new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2delPosProcessor = new EnumMap<>(BaseSub.class);

		for (final BaseSub baseSub : baseSubs) {
			final PositionProcessor alignedPosProcessor = new PositionProcessor();
			// deletions don't need validation
			alignedPosProcessor.addValidator(new CombinedValidator(validators));
			final AbstractBaseCallCountStorage arrestBccStorage = new DefaultBCCStorage(sharedStorage,
					getDataContainerBuilderFactory().arrestBccDt);

			final AbstractBaseCallCountStorage throughBccStorage = new DefaultBCCStorage(sharedStorage,
					getDataContainerBuilderFactory().throughBccDt);

			final Storage rtArrestCountStorage = new RTarrestCountStorage(sharedStorage, locInterpreter,
					arrestBccStorage, throughBccStorage);

			cache.addStorage(rtArrestCountStorage);
			alignedPosProcessor.addStorage(rtArrestCountStorage);
			baseSub2alignedPosProcessor.put(baseSub, alignedPosProcessor);

			if (parameter.showInsertionCount() || parameter.showDeletionCount()) {
				final PositionProcessor covPosProcessor = new PositionProcessor();
				final DataType<IntegerData> covDataType = DataType.retrieve(baseSub + "_coverage", IntegerData.class,
						new ExtractBaseSub(baseSub, getDataContainerBuilderFactory().bs2coverageDt));
				final Storage covStorage = new ArrayIntegerStorage(sharedStorage, covDataType);
				cache.addStorage(covStorage);
				covPosProcessor.addStorage(covStorage);
				baseSub2covPosProcessor.put(baseSub, covPosProcessor);
			}
			if (parameter.showInsertionCount()) {
				final PositionProcessor insPosProcessor = new PositionProcessor();
				final DataType<IntegerData> insDataType = DataType.retrieve(baseSub + "_insertions", IntegerData.class,
						new ExtractBaseSub(baseSub, getDataContainerBuilderFactory().bs2insertionsDt));
				final Storage insStorage = new MapIntegerStorage(sharedStorage, insDataType);
				cache.addStorage(insStorage);
				insPosProcessor.addStorage(insStorage);
				baseSub2delPosProcessor.put(baseSub, insPosProcessor);
			}
			if (parameter.showDeletionCount()) {
				final PositionProcessor delPosProcessor = new PositionProcessor();
				final DataType<IntegerData> delDataType = DataType.retrieve(baseSub + "_deletions", IntegerData.class,
						new ExtractBaseSub(baseSub, getDataContainerBuilderFactory().bs2insertionsDt));
				final Storage delStorage = new MapIntegerStorage(sharedStorage, delDataType);
				cache.addStorage(delStorage);
				delPosProcessor.addStorage(delStorage);
				baseSub2delPosProcessor.put(baseSub, delPosProcessor);
			}
		}

		final BaseCallInterpreter bci = BaseCallInterpreter.build(conditionParameter.getLibraryType());

		cache.addRecordProcessor(new BaseSubRecordProcessor(sharedStorage, bci, new CombinedValidator(validators),
				baseSubs, baseSub2alignedPosProcessor, baseSub2covPosProcessor, baseSub2insPosProcessor,
				baseSub2delPosProcessor));
		return cache;
	}

}
