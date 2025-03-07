package lib.data.assembler.factory;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.SiteDataAssembler;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.Cache;
import lib.data.storage.Storage;
import lib.data.storage.indel.InsertionStorage;
import lib.data.storage.indel.DeletionStorage;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.integer.ArrayIntegerStorage;
import lib.data.storage.processor.CoverageRecordProcessor;
import lib.data.storage.processor.DeletionRecordProcessor;
import lib.data.storage.processor.InsertionRecordProcessor;
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
								DataType.PILEUP_COUNT.getFetcher()));
		}
	}
	
	protected void addInsertionCache(
			final GeneralParameter parameter, 
			final SharedStorage sharedStorage,
			final Cache cache) {
		
		if (parameter.showInsertionCount() || parameter.showInsertionStartCount()) {
			final boolean onlyStart = parameter.showInsertionStartCount();
			cache.addCache(createInsertionCache(
							sharedStorage, 
							DataType.PILEUP_COUNT.getFetcher(),
							onlyStart));
		}
	}

	Cache createCoverageCache(final SharedStorage sharedStorage, final Fetcher<IntegerData> covFetcher) {
		final Cache cache = new Cache();
		final Storage covStorage = new ArrayIntegerStorage(sharedStorage, covFetcher);
		cache.addStorage(covStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController()
				.getCoordinateTranslator();

		cache.addRecordProcessor(new CoverageRecordProcessor(translator, covStorage));
		return cache;
	}

	Cache createDeletionCache(
			final SharedStorage sharedStorage, final Fetcher<PileupCount> delFetcher) {

		final Cache cache = new Cache();

		final Storage delStorage = new DeletionStorage(sharedStorage, delFetcher);
		cache.addStorage(delStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController()
				.getCoordinateTranslator();

		cache.addRecordProcessor(new DeletionRecordProcessor(translator, delStorage));

		return cache;
	}

	Cache createInsertionCache(
			final SharedStorage sharedStorage, final Fetcher<PileupCount> insFetcher, final boolean onlyStart) {
		
		final Cache cache = new Cache();
		
		final Storage insStorage = new InsertionStorage(sharedStorage, insFetcher);
		cache.addStorage(insStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController()
				.getCoordinateTranslator();

		cache.addRecordProcessor(new InsertionRecordProcessor(translator, insStorage, onlyStart));
		
		return cache;
	}
	
}
