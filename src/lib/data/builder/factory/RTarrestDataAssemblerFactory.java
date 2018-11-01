package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.rtarrest.RTarrestMethod.RTarrestBuilderFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.DataType;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.DefaultBaseCallAdder;
import lib.data.cache.FR_SECONDSTRAND_RTarrestDataCache;
import lib.data.cache.RF_FIRSTSTRAND_RTarrestDataCache;
import lib.data.cache.UNSTRANDED_RTarrestDataCache;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.basecall.BaseCallCountExtractor;
import lib.data.cache.readsubstitution.StrandedReadSubstitutionCache;
import lib.data.cache.readsubstitution.UnstrandedReadSubstitutionCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;

public class RTarrestDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	public RTarrestDataAssemblerFactory(final RTarrestBuilderFactory builderFactory) {
		super(builderFactory);
	}

	@Override
	public List<RecordWrapperDataCache> createCaches(
			final AbstractParameter parameter,
			final SharedCache sharedCache, 
			final AbstractConditionParameter conditionParameter) {

		final List<RecordWrapperDataCache> dataCaches = new ArrayList<>(3);

		final byte minBASQ = conditionParameter.getMinBASQ();
		final LibraryType libraryType = conditionParameter.getLibraryType();
		
		RecordWrapperDataCache dataCache;
		switch (libraryType) {

			case UNSTRANDED:
				dataCache = new UNSTRANDED_RTarrestDataCache(
						minBASQ, 
						sharedCache);
				break;
	
			case RF_FIRSTSTRAND:
				dataCache = new RF_FIRSTSTRAND_RTarrestDataCache(
						minBASQ, 
						sharedCache);
				break;
	
			case FR_SECONDSTRAND:
				dataCache = new FR_SECONDSTRAND_RTarrestDataCache(
						minBASQ, 
						sharedCache);
				break;
				
			default:
				throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
		dataCaches.add(dataCache);

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
		return dataCaches;
	}

}
