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
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.SiteDataAssembler;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.basecall.BaseCallCountExtractor;
import lib.data.storage.Cache;
import lib.data.storage.basecall.DefaultBaseCallCountStorage;
import lib.data.storage.container.CacheContainer;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubstitutionRecordProcessor;
import lib.data.stroage.Storage;
import lib.data.validator.CombinedValidator;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.Util;

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
		
		final Map<BaseSubstitution, Storage> baseSub2storage = new HashMap<>(
				Util.noRehashCapacity(baseSubs.size()));
		for (final BaseSubstitution baseSub : baseSubs) {
			final Fetcher<BaseCallCount> bccFetcher = new BaseCallCountExtractor(
					baseSub, 
					DataType.BASE_SUBST.getFetcher());
			final Storage storage = new DefaultBaseCallCountStorage(
					sharedStorage, 
					bccFetcher);
			cache.addStorage(storage);
			baseSub2storage.put(baseSub, storage);
		}
		
		cache.addRecordProcessor(new BaseSubstitutionRecordProcessor(
				sharedStorage, 
				bci, 
				new CombinedValidator(validators), 
				baseSub2storage) );
	}
	
}
