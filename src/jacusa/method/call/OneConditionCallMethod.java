package jacusa.method.call;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lib.data.DataType;
import lib.data.assembler.factory.CallDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanData;
import lib.data.filter.BooleanFilteredData;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.KnownReferenceBase;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;

import org.apache.commons.cli.ParseException;

public class OneConditionCallMethod 
extends CallMethod {

	protected OneConditionCallMethod(
			final String name, 
			final CallParameter parameter, 
			final CallDataAssemblerFactory dataAssemblerFactory) {
		
		super(name, parameter, dataAssemblerFactory);
	}

	@Override
	public Map<Character, FilterFactory> getFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanData = 
				new DefaultFilteredDataFetcher<BooleanFilteredData, BooleanData>(DataType.F_BOOLEAN);
		
		return Arrays.asList(
				new ExcludeSiteFilterFactory(),
				new CombinedFilterFactory(
						getBaseCallCountFetcher(),
						filteredBccData),
				new INDELfilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new ReadPositionFilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new SpliceSiteFilterFactory(
						getBaseCallCountFetcher(), 
						filteredBccData),
				new MaxAlleleCountFilterFactory(getBaseCallCountFetcher()),
				new HomopolymerFilterFactory(getParameter(), filteredBooleanData))
				.stream()
				.collect(Collectors.toMap(FilterFactory::getID, Function.identity()) );
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		final List<ParallelDataValidator> validators = new ArrayList<ParallelDataValidator>();
		validators.add(new KnownReferenceBase());
		validators.add(new MinCoverageValidator(getBaseCallCountFetcher(), getParameter().getConditionParameters()));

		if (! this.getParameter().showAllSites()) {
			validators.add(new ExtendedVariantSiteValidator(getBaseCallCountFetcher()));
		}
		return validators;
	}
	
	@Override
	public void parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new ParseException("BAM File is not provided!");
		}
		
		super.parseArgs(args);
	}
	
}
