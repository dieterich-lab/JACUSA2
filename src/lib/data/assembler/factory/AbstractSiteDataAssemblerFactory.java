package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.filter.FilterContainer;
import lib.cli.options.filter.has.BaseSub;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.SiteDataAssembler;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.basecall.BaseCallCountExtractor;
import lib.data.fetcher.basecall.IntegerDataExtractor;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.DefaultBCCStorage;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.integer.ArrayIntegerStorage;
import lib.data.storage.integer.MapIntegerStorage;
import lib.data.storage.processor.DeletionRecordProcessor;
import lib.data.storage.processor.InsertionRecordProcessor;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.coordinate.CoordinateTranslator;

public abstract class AbstractSiteDataAssemblerFactory
extends AbstractDataAssemblerFactory {
	
	public AbstractSiteDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		super(builderFactory);
	}
	
	@Override
	public DataAssembler newInstance(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter, 
			final int replicateIndex) {

		final CacheContainer cacheContainer = createContainer(
				parameter, filterContainer, sharedStorage, conditionParameter);
		return new SiteDataAssembler(
				replicateIndex, 
				getBuilderFactory(), 
				conditionParameter, 
				cacheContainer);
	}
	
	protected void addDeletionCache(
			final GeneralParameter parameter, 
			final SharedStorage sharedStorage,
			final Cache cache) {
		
		if (parameter.showDeletionCount()) {
				cache.addCache(createDeletionCache(
								sharedStorage, 
								DataType.COVERAGE.getFetcher(), 
								DataType.DELETION_COUNT.getFetcher()));
		}
	}
	
	protected void addInsertionCache(
			final GeneralParameter parameter, 
			final SharedStorage sharedStorage,
			final Cache cache) {
		
		if (parameter.showInsertionCount()) {
				cache.addCache(createInsertionCache(
								sharedStorage, 
								DataType.COVERAGE.getFetcher(), 
								DataType.INSERTION_COUNT.getFetcher()));
		}
	}

	Cache createDeletionCache(
			final SharedStorage sharedStorage, 
			final Fetcher<IntegerData> covFetcher, final Fetcher<IntegerData> delFetcher) {
		
		final Cache cache = new Cache();
		final Storage covStorage = new ArrayIntegerStorage(sharedStorage, covFetcher);
		cache.addStorage(covStorage);
		
		final Storage delStorage = new MapIntegerStorage(sharedStorage, delFetcher);
		cache.addStorage(delStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController()
				.getCoordinateTranslator();

		cache.addRecordProcessor(new DeletionRecordProcessor(
				translator,
				covStorage, delStorage));
		
		return cache;
	}

	Cache createInsertionCache(
			final SharedStorage sharedStorage, 
			final Fetcher<IntegerData> covFetcher, final Fetcher<IntegerData> insFetcher) {
		
		final Cache cache = new Cache();
		final Storage covStorage = new ArrayIntegerStorage(sharedStorage, covFetcher);
		cache.addStorage(covStorage);
		
		final Storage insStorage = new MapIntegerStorage(sharedStorage, insFetcher);
		cache.addStorage(insStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController()
				.getCoordinateTranslator();

		cache.addRecordProcessor(new InsertionRecordProcessor(
				translator,
				covStorage, insStorage));
		
		return cache;
	}
	
	// TODO
	protected void stratifyByBaseSubstitution(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter,
			final Cache cache) {

		final SortedSet<BaseSub> baseSubs = parameter.getReadSubstitutions();
		if (baseSubs.isEmpty()) {
			return;
		}

		final byte minBASQ = conditionParameter.getMinBASQ();
		final List<Validator> validators = new ArrayList<>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQValidator(minBASQ));
		}

		final BaseCallInterpreter bci = BaseCallInterpreter.build(conditionParameter.getLibraryType());

		final Map<BaseSub, PositionProcessor> baseSub2alignedPosProcessor = 
				new EnumMap<>(BaseSub.class);

		final Map<BaseSub, PositionProcessor> baseSub2covPosProcessor = 
				new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2insPosProcessor = 
				new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2delPosProcessor = 
				new EnumMap<>(BaseSub.class);

		for (final BaseSub baseSub : baseSubs) {
			final PositionProcessor alignedPosProcessor = new PositionProcessor();
			// deletions don't need validation
			alignedPosProcessor.addValidator(new CombinedValidator(validators));
			final Fetcher<BaseCallCount> bccFetcher = new BaseCallCountExtractor(
					baseSub, 
					DataType.BASE_SUBST2BCC.getFetcher());
			final Storage bccStorage = new DefaultBCCStorage(
					sharedStorage, 
					bccFetcher);
			cache.addStorage(bccStorage);
			alignedPosProcessor.addStorage(bccStorage);
			baseSub2alignedPosProcessor.put(baseSub, alignedPosProcessor);
			
			if (parameter.showInsertionCount() || parameter.showDeletionCount()) {
				final PositionProcessor covPosProcessor = new PositionProcessor();
				final Fetcher<IntegerData> covFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2COVERAGE.getFetcher());
				final Storage covStorage = new ArrayIntegerStorage(
						sharedStorage, 
						covFetcher);
				cache.addStorage(covStorage);
				covPosProcessor.addStorage(covStorage);
				baseSub2covPosProcessor.put(baseSub, covPosProcessor);
			}
			if (parameter.showInsertionCount()) {
				final PositionProcessor insPosProcessor = new PositionProcessor();
				final Fetcher<IntegerData> insFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2INSERTION_COUNT.getFetcher());
				final Storage insStorage = new MapIntegerStorage(
						sharedStorage, 
						insFetcher);
				cache.addStorage(insStorage);
				insPosProcessor.addStorage(insStorage);
				baseSub2insPosProcessor.put(baseSub, insPosProcessor);
			}
			if (parameter.showDeletionCount()) {
				final PositionProcessor delPosProcessor = new PositionProcessor();
				final Fetcher<IntegerData> delFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2DELETION_COUNT.getFetcher());
				final Storage delStorage = new MapIntegerStorage(
						sharedStorage, 
						delFetcher);
				cache.addStorage(delStorage);
				delPosProcessor.addStorage(delStorage);
				baseSub2delPosProcessor.put(baseSub, delPosProcessor);
			}
		}
		
		cache.addRecordProcessor(
				new BaseSubRecordProcessor(
				sharedStorage, 
				bci, 
				new CombinedValidator(validators),
				baseSubs,
				baseSub2alignedPosProcessor,
				baseSub2covPosProcessor,
				baseSub2insPosProcessor,
				baseSub2delPosProcessor));
	}
	
}
