package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.filter.FilterContainer;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
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
import lib.data.storage.basecall.DefaultBaseCallCountStorage;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.integer.MapIntegerStorage;
import lib.data.storage.processor.DeletionRecordProcessor;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubstitutionRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.Util;
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
			final int replicateIndex)
			throws IllegalArgumentException {
		
		final CacheContainer cacheContainer = createContainer(
				parameter, filterContainer, sharedStorage, conditionParameter, replicateIndex);
		return new SiteDataAssembler(
				replicateIndex, 
				getBuilderFactory(), 
				conditionParameter, 
				cacheContainer);
	}
	
	protected void addDelectionCount(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter,
			final Cache cache) {

		final CoordinateTranslator translator = 
				sharedStorage.getCoordinateController().getCoordinateTranslator();
		if (parameter.showDeletionCount()) {
			final Fetcher<IntegerData> delFetcher = DataType.DELETION_COUNT.getFetcher();
			final Storage deletionStorage = new MapIntegerStorage(sharedStorage, delFetcher);
			cache.addRecordProcessor(
					new DeletionRecordProcessor(translator, new PositionProcessor(deletionStorage)));
		}
	}
	
	protected void addBaseSubstitution(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter,
			final Cache cache) {

		final SortedSet<BaseSubstitution> baseSubs = parameter.getReadSubstitutions();
		if (baseSubs.size() == 0) {
			return;
		}
		
		final byte minBASQ = conditionParameter.getMinBASQ();
		final List<Validator> validators = new ArrayList<Validator>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQValidator(minBASQ));
		}

		final BaseCallInterpreter bci = 
				BaseCallInterpreter.build(conditionParameter.getLibraryType());

		int size = baseSubs.size();
		if (parameter.showDeletionCount()) {
			size *= 2;
		}
		final Map<BaseSubstitution, Storage> baseSub2storage = new HashMap<>(
				Util.noRehashCapacity(size));

		final PositionProcessor alignPosProcessor 	= new PositionProcessor();
		alignPosProcessor.addValidator(new CombinedValidator(validators));
		// deletions don't need validation
		final PositionProcessor deletedPosProcessor = new PositionProcessor();
		
		for (final BaseSubstitution baseSub : baseSubs) {
			final Fetcher<BaseCallCount> bccFetcher = new BaseCallCountExtractor(
					baseSub, 
					DataType.BASE_SUBST2BCC.getFetcher());
			final Storage bccStorage = new DefaultBaseCallCountStorage(
					sharedStorage, 
					bccFetcher);
			cache.addStorage(bccStorage);
			baseSub2storage.put(baseSub, bccStorage);
			alignPosProcessor.addStorage(bccStorage);
			
			if (parameter.showDeletionCount()) {
				final Fetcher<IntegerData> delFetcher = new IntegerDataExtractor(
						baseSub, 
						DataType.BASE_SUBST2DELETION_COUNT.getFetcher());
				final Storage delStorage = new MapIntegerStorage(
						sharedStorage, 
						delFetcher);
				cache.addStorage(delStorage);
				baseSub2storage.put(baseSub, delStorage);
				deletedPosProcessor.addStorage(delStorage);
			}
		}
		
		final boolean stratifyOnlyBcc = ! parameter.showDeletionCount();
		
		final List<PositionProcessor> positionProcessors = new ArrayList<>(2);
		positionProcessors.add(alignPosProcessor);
		if (parameter.showDeletionCount()) {
			positionProcessors.add(deletedPosProcessor);
		}
		
		cache.addRecordProcessor(
				new BaseSubstitutionRecordProcessor(
				sharedStorage, 
				bci, 
				new CombinedValidator(validators),
				stratifyOnlyBcc,
				baseSub2storage.keySet(), 
				positionProcessors) );
		
		
	}
	
}
