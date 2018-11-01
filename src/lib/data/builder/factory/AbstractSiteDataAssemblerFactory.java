package lib.data.builder.factory;

import java.util.List;

import jacusa.filter.FilterContainer;
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
import lib.data.cache.fetcher.basecall.BaseCallCountExtractor;
import lib.data.cache.readsubstitution.StrandedReadSubstitutionCache;
import lib.data.cache.readsubstitution.UnstrandedReadSubstitutionCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;

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

		final int baseSubSize = parameter.getReadSubstitutions().size();
		if (baseSubSize > 0) {
			final MinBASQBaseCallValidator minBASQBaseCallValidator = 
					new MinBASQBaseCallValidator(conditionParameter.getMinBASQ());
			// create base substitution specific counters
			final IncrementAdder[] substBccAdders = 
					parameter.getReadSubstitutions().stream()
					.map(
							bs -> new DefaultBaseCallAdder(
									sharedCache, 
									new BaseCallCountExtractor(bs, DataType.BASE_SUBST.getFetcher())))
					.toArray(IncrementAdder[]::new);

			if (conditionParameter.getLibraryType() == LibraryType.UNSTRANDED) {
				dataCaches.add(
						new UnstrandedReadSubstitutionCache(
								sharedCache,
								minBASQBaseCallValidator,
								parameter.getReadSubstitutions(),
								substBccAdders) );	
			} else {
				dataCaches.add(
						new StrandedReadSubstitutionCache(
								sharedCache,
								minBASQBaseCallValidator,
								parameter.getReadSubstitutions(),
								substBccAdders) );
			}	
		}
	}
	
}
