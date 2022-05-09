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
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.KnownReferenceBase;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;

import org.apache.commons.cli.ParseException;

public class OneConditionCallMethod extends CallMethod {

	protected OneConditionCallMethod(final String name, final CallParameter parameter,
			final CallDataAssemblerFactory dataAssemblerFactory) {

		super(name, parameter, dataAssemblerFactory);
	}

	@Override
	public Map<Character, FilterFactory> getFilterFactories() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<FilteredBaseCallCount> filteredBccDt = getDataAssemblerFactory()
				.getDataContainerBuilderFactory().filteredBccDt;
		final DataType<FilteredBoolean> filteredBooleanDt = getDataAssemblerFactory()
				.getDataContainerBuilderFactory().filteredBooleanDt;

		return Arrays.asList(new ExcludeSiteFilterFactory(), new CombinedFilterFactory(bccDt, filteredBccDt),
				new INDELfilterFactory(bccDt, filteredBccDt), new ReadPositionFilterFactory(bccDt, filteredBccDt),
				new SpliceSiteFilterFactory(bccDt, filteredBccDt), new MaxAlleleCountFilterFactory(bccDt),
				new HomopolymerFilterFactory(getParameter(), filteredBooleanDt)).stream()
				.collect(Collectors.toMap(FilterFactory::getID, Function.identity()));
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;

		final List<ParallelDataValidator> validators = new ArrayList<ParallelDataValidator>();
		validators.add(new KnownReferenceBase());
		validators.add(new MinCoverageValidator(bccDt, getParameter().getConditionParameters()));

		if (!this.getParameter().showAllSites()) {
			validators.add(new ExtendedVariantSiteValidator(bccDt));
		}
		return validators;
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new ParseException("BAM File is not provided!");
		}
		return super.parseArgs(args);
	}

}
