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
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.DataContainer.AbstractDataContainerBuilder;
import lib.data.DataContainer.AbstractDataContainerBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.SiteDataAssembler;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.DefaultBCCStorage;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.FRPairedEnd2CacheContainer;
import lib.data.storage.container.RFPairedEnd1CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.container.UnstrandedCacheContainter;
import lib.data.storage.integer.ArrayIntegerStorage;
import lib.data.storage.integer.MapIntegerStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;

public abstract class AbstractDataAssemblerFactory<T extends AbstractDataContainerBuilderFactory> {

	private final T dataContainerBuilderFactory;

	public AbstractDataAssemblerFactory(final T dataContainerBuilderFactory) {
		this.dataContainerBuilderFactory = dataContainerBuilderFactory;
	}

	public DataAssembler newInstance(final GeneralParameter parameter, final FilterContainer filterContainer,
			final SharedStorage sharedStorage, final ConditionParameter conditionParameter, final int replicateI) {

		final CacheContainer cacheContainer = createContainer(parameter, filterContainer, sharedStorage,
				conditionParameter);
		return new SiteDataAssembler(replicateI, getDataContainerBuilderFactory(), conditionParameter, cacheContainer);
	}

	public T getDataContainerBuilderFactory() {
		return dataContainerBuilderFactory;
	}

	protected CacheContainer createContainer(final GeneralParameter parameter, final FilterContainer filterContainer,
			final SharedStorage sharedStorage, final ConditionParameter conditionParameter) {

		CacheContainer cacheContainer = null;

		switch (conditionParameter.getLibraryType()) {

		case RF_FIRSTSTRAND: {
			final CacheContainer forwardCacheContainer = new UnstrandedCacheContainter(sharedStorage,
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));
			final CacheContainer reverseCacheContainer = new UnstrandedCacheContainter(sharedStorage,
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));

			cacheContainer = new RFPairedEnd1CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}

		case FR_SECONDSTRAND: {
			final CacheContainer forwardCacheContainer = new UnstrandedCacheContainter(sharedStorage,
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));
			final CacheContainer reverseCacheContainer = new UnstrandedCacheContainter(sharedStorage,
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));

			cacheContainer = new FRPairedEnd2CacheContainer(forwardCacheContainer, reverseCacheContainer);
			break;
		}

		case UNSTRANDED: {
			cacheContainer = new UnstrandedCacheContainter(sharedStorage,
					combineCaches(parameter, filterContainer, sharedStorage, conditionParameter));
			break;
		}

		case MIXED:
			throw new IllegalArgumentException(
					"Cannot create cache for library type: " + conditionParameter.getLibraryType().toString());
		}

		return cacheContainer;
	}

	protected abstract Cache createCache(final GeneralParameter parameter, final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter);

	// combine data and filter cache
	private Cache combineCaches(final GeneralParameter parameter, final FilterContainer filterContainer,
			final SharedStorage sharedStorage, final ConditionParameter conditionParameter) {

		final Cache cache = new Cache();
		// create cache for data gathering
		cache.addCache(createCache(parameter, sharedStorage, conditionParameter));
		// create cache for data filtering
		cache.addCache(filterContainer.createFilterCache(conditionParameter, sharedStorage));
		return cache;
	}

	protected void stratifyByBaseSub(final GeneralParameter parameter, final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter, final Cache cache) {

		final SortedSet<BaseSub> baseSubs = parameter.getReadTags();
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

		final Map<BaseSub, PositionProcessor> baseSub2algnPosProc = new EnumMap<>(BaseSub.class);

		final Map<BaseSub, PositionProcessor> baseSub2covPosProc = new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2insPosProc = new EnumMap<>(BaseSub.class);
		final Map<BaseSub, PositionProcessor> baseSub2delPosProc = new EnumMap<>(BaseSub.class);

		for (final BaseSub baseSub : baseSubs) {
			final PositionProcessor algnPosProc = new PositionProcessor();
			// deletions don't need validation
			algnPosProc.addValidator(new CombinedValidator(validators));
			final Fetcher<BaseCallCount> bccFetcher = new BCCextractor(baseSub, DataType.BASE_SUBST2BCC.getFetcher());
			final Storage bccStorage = new DefaultBCCStorage(sharedStorage, bccFetcher);
			cache.addStorage(bccStorage);
			algnPosProc.addStorage(bccStorage);
			baseSub2algnPosProc.put(baseSub, algnPosProc);

			if (parameter.showInsertionCount() || parameter.showDeletionCount()) {
				final PositionProcessor covPosProc = new PositionProcessor();
				final Fetcher<IntegerData> covFetcher = new IntegerDataExtractor(baseSub,
						DataType.BASE_SUBST2COVERAGE.getFetcher());
				final Storage covStorage = new ArrayIntegerStorage(sharedStorage, covFetcher);
				cache.addStorage(covStorage);
				covPosProc.addStorage(covStorage);
				baseSub2covPosProc.put(baseSub, covPosProc);
			}
			if (parameter.showInsertionCount()) {
				final PositionProcessor insPosProc = new PositionProcessor();
				final Fetcher<IntegerData> insFetcher = new IntegerDataExtractor(baseSub,
						DataType.BASE_SUBST2INSERTION_COUNT.getFetcher());
				final Storage insStorage = new MapIntegerStorage(sharedStorage, insFetcher);
				cache.addStorage(insStorage);
				insPosProc.addStorage(insStorage);
				baseSub2insPosProc.put(baseSub, insPosProc);
			}
			if (parameter.showDeletionCount()) {
				final PositionProcessor delPosProc = new PositionProcessor();
				final Fetcher<IntegerData> delFetcher = new IntegerDataExtractor(baseSub,
						DataType.BASE_SUBST2DELETION_COUNT.getFetcher());
				final Storage delStorage = new MapIntegerStorage(sharedStorage, delFetcher);
				cache.addStorage(delStorage);
				delPosProc.addStorage(delStorage);
				baseSub2delPosProc.put(baseSub, delPosProc);
			}
		}

		cache.addRecordProcessor(new BaseSubRecordProcessor(sharedStorage, bci, new CombinedValidator(validators),
				baseSubs, baseSub2algnPosProc, baseSub2covPosProc, baseSub2insPosProc, baseSub2delPosProc));
	}

}
