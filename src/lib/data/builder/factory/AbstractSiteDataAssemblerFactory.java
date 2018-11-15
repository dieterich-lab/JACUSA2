package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jacusa.filter.FilterContainer;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.DataType;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.DefaultBaseCallAdder;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.SiteDataAssembler;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.basecall.BaseCallCountExtractor;
import lib.data.cache.readsubstitution.BaseCallInterpreter;
import lib.data.cache.readsubstitution.ReadSubstitutionCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.IntegrateValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.count.basecall.BaseCallCount;

public abstract class AbstractSiteDataAssemblerFactory
extends AbstractDataAssemblerFactory {
	
	public AbstractSiteDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		super(builderFactory);
	}
	
	@Override
	public DataAssembler newInstance(
			final AbstractParameter parameter,
			final FilterContainer filterContainer,
			final SharedCache sharedCache, 
			final AbstractConditionParameter conditionParameter, 
			final int replicateIndex)
			throws IllegalArgumentException {
		
		final CacheContainer cacheContainer = createContainer(
				parameter, filterContainer, sharedCache, conditionParameter, replicateIndex);
		return new SiteDataAssembler(
				replicateIndex, 
				getBuilderFactory(), 
				conditionParameter, 
				cacheContainer);
	}
	
	protected void addBaseSubstitution(
			final AbstractParameter parameter,
			final SharedCache sharedCache, 
			final AbstractConditionParameter conditionParameter,
			final List<RecordWrapperDataCache> dataCaches) {

		final SortedSet<BaseSubstitution> baseSubs = parameter.getReadSubstitutions();
		if (baseSubs.size() > 0) {
			final byte minBASQ = conditionParameter.getMinBASQ();
			final List<BaseCallValidator> validators = new ArrayList<BaseCallValidator>();
			validators.add(new DefaultBaseCallValidator());
			if (minBASQ > 0) {
				validators.add(new MinBASQBaseCallValidator(minBASQ));
			}

			final BaseCallInterpreter bci = 
					BaseCallInterpreter.create(conditionParameter.getLibraryType());
			
			final Map<BaseSubstitution, IncrementAdder> sub2adder = new HashMap<>(baseSubs.size());
			for (final BaseSubstitution baseSub : baseSubs) {
				final Fetcher<BaseCallCount> bccFetcher = 
						new BaseCallCountExtractor(
								baseSub, 
								DataType.BASE_SUBST.getFetcher());
				final IncrementAdder adder = 
						new DefaultBaseCallAdder(
								sharedCache, 
								bccFetcher);
				sub2adder.put(baseSub, adder);
			}
			
			dataCaches.add(
					new ReadSubstitutionCache(
						sharedCache, 
						bci, 
						new IntegrateValidator(validators), 
						sub2adder) );
			
		}
	}
	
}
