package jacusa.method.call;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.DataType;
import lib.data.builder.factory.CallDataAssemblerFactory;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;

import org.apache.commons.cli.ParseException;

public class OneConditionCallMethod 
extends CallMethod {

	protected OneConditionCallMethod(
			final String name, 
			final CallParameter parameter, 
			final CallDataAssemblerFactory dataAssemblerFactory,
			final CallBuilderFactory builderFactory) {
		
		super(name, parameter, dataAssemblerFactory, builderFactory);
	}

	@Override
	public Map<Character, AbstractFilterFactory> getFilterFactories() {
		final Map<Character, AbstractFilterFactory> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory>();

		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanData = 
				new DefaultFilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper>(DataType.F_BOOLEAN);
		
		final List<AbstractFilterFactory> filterFactories = Arrays.asList(
				new ExcludeSiteFilterFactory(),
				new CombinedFilterFactory(
						getBaseCallCountFetcher(),
						filteredBccData),
				new INDEL_FilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new ReadPositionDistanceFilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new SpliceSiteFilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new MaxAlleleCountFilterFactory(getBaseCallCountFetcher()),
				new HomopolymerFilterFactory(filteredBooleanData) );

		for (final AbstractFilterFactory filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(
				new MinCoverageValidator(getBaseCallCountFetcher(), getParameter().getConditionParameters()),
				new ExtendedVariantSiteValidator(getBaseCallCountFetcher()));
	}
	
	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new ParseException("BAM File is not provided!");
		}
		return super.parseArgs(args);
	}
	
}
