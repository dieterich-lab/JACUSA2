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
import lib.data.fetcher.basecall.BCCextractor;
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

abstract class AbstractSiteDataAssemblerFactory
extends AbstractDataAssemblerFactory {
	
	AbstractSiteDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		super(builderFactory);
	}
	
	@Override
	public DataAssembler newInstance(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter, 
			final int replicateI) {

		final CacheContainer cacheContainer = createContainer(
				parameter, filterContainer, sharedStorage, conditionParameter);
		return new SiteDataAssembler(
				replicateI, 
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
	
	protected void stratifyByBaseSub(
			final GeneralParameter prm,
			final SharedStorage sharedStorage, 
			final ConditionParameter condPrm,
			final Cache cache) {

		final SortedSet<BaseSub> baseSubs = prm.getReadTags();
		if (baseSubs.isEmpty()) {
			return;
		}

		final byte minBASQ = condPrm.getMinBASQ();
		final List<Validator> validators = new ArrayList<>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQValidator(minBASQ));
		}

		final BaseCallInterpreter bci = BaseCallInterpreter.build(condPrm.getLibraryType());

		final Map<BaseSub, PositionProcessor> baseSub2algnPosProc = 
				new EnumMap<>(BaseSub.class);

		final Map<BaseSub, PositionProcessor> baseSub2covPosProc = 
				new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2insPosProc = 
				new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2delPosProc = 
				new EnumMap<>(BaseSub.class);

		for (final BaseSub baseSub : baseSubs) {
			final PositionProcessor algnPosProc = new PositionProcessor();
			// deletions don't need validation
			algnPosProc.addValidator(new CombinedValidator(validators));
			final Fetcher<BaseCallCount> bccFetcher = new BCCextractor(
					baseSub, 
					DataType.BASE_SUBST2BCC.getFetcher());
			final Storage bccStorage = new DefaultBCCStorage(
					sharedStorage, 
					bccFetcher);
			cache.addStorage(bccStorage);
			algnPosProc.addStorage(bccStorage);
			baseSub2algnPosProc.put(baseSub, algnPosProc);
			
			if (prm.showInsertionCount() || prm.showDeletionCount()) {
				final PositionProcessor covPosProc = new PositionProcessor();
				final Fetcher<IntegerData> covFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2COVERAGE.getFetcher());
				final Storage covStorage = new ArrayIntegerStorage(
						sharedStorage, 
						covFetcher);
				cache.addStorage(covStorage);
				covPosProc.addStorage(covStorage);
				baseSub2covPosProc.put(baseSub, covPosProc);
			}
			if (prm.showInsertionCount()) {
				final PositionProcessor insPosProc = new PositionProcessor();
				final Fetcher<IntegerData> insFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2INSERTION_COUNT.getFetcher());
				final Storage insStorage = new MapIntegerStorage(
						sharedStorage, 
						insFetcher);
				cache.addStorage(insStorage);
				insPosProc.addStorage(insStorage);
				baseSub2insPosProc.put(baseSub, insPosProc);
			}
			if (prm.showDeletionCount()) {
				final PositionProcessor delPosProc = new PositionProcessor();
				final Fetcher<IntegerData> delFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2DELETION_COUNT.getFetcher());
				final Storage delStorage = new MapIntegerStorage(
						sharedStorage, 
						delFetcher);
				cache.addStorage(delStorage);
				delPosProc.addStorage(delStorage);
				baseSub2delPosProc.put(baseSub, delPosProc);
			}
		}
		
		cache.addRecordProcessor(
				new BaseSubRecordProcessor(
				sharedStorage, 
				bci, 
				new CombinedValidator(validators),
				baseSubs,
				baseSub2algnPosProc,
				baseSub2covPosProc,
				baseSub2insPosProc,
				baseSub2delPosProc));
	}
	
}
