package jacusa.method.lrtarrest;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestCombinedFilterFactory;
import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat;
import jacusa.method.rtarrest.DummyStatisticFactory;
import jacusa.worker.LRTarrestWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MaxDepthConditionOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagConditionOption;
import lib.cli.options.condition.filter.FilterNMsamTagConditionOption;
import lib.data.DataType;
import lib.data.assembler.factory.LRTarrestDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.LRTarrestVariantParallelPileup;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.betabin.LRTarrestStatFactory;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class LRTarrestMethod extends AbstractMethod<LRTarrestDataContainerBuilderFactory> {

	/*
	 * TODO remove private final Fetcher<ArrestPos2BCC> ap2bccFetcher; private final
	 * Fetcher<BaseCallCount> totalBccFetcher; private final Fetcher<BaseCallCount>
	 * arrestBccExtractor; private final Fetcher<BaseCallCount> throughBccExtractor;
	 */

	private LRTarrestMethod(final String name, final LRTarrestParameter parameter,
			final LRTarrestDataAssemblerFactory dataAssemblerFactory) {

		super(name, parameter, dataAssemblerFactory);
	}

	protected void initGlobalACOptions() {
		addACOption(new StatFactoryOption(getParameter().getStatParameter(), getStatistics()));

		addACOption(new StatFilterOption(getParameter().getStatParameter()));

		// result format option only if there is a choice
		if (getResultFormats().size() > 1) {
			addACOption(new ResultFormatOption(getParameter(), getResultFormats()));
		}

		addACOption(new FilterModusOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));

		addACOption(new ReferenceFastaFilenameOption(getParameter()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));

		addACOption(new DebugModusOption(getParameter(), this));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));

		addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters()));

		final Set<LibraryType> availableLibType = new HashSet<>(
				Arrays.asList(LibraryType.RF_FIRSTSTRAND, LibraryType.FR_SECONDSTRAND));

		addACOption(new nConditionLibraryTypeOption(availableLibType, getParameter().getConditionParameters(),
				getParameter()));

		// condition specific
		for (int condI = 0; condI < getParameter().getConditionsSize(); ++condI) {
			addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new MaxDepthConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(condI)));

			addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(condI)));

			addACOption(new nConditionLibraryTypeOption(availableLibType,
					getParameter().getConditionParameters().get(condI), getParameter()));
		}
	}

	public Map<String, AbstractStatFactory> getStatistics() {
		final Map<String, AbstractStatFactory> factories = new TreeMap<>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<>(5);
		tmpFactory.add(new DummyStatisticFactory());
		tmpFactory.add(new LRTarrestStatFactory());

		for (final AbstractStatFactory factory : tmpFactory) {
			factories.put(factory.getName(), factory);
		}

		return factories;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<FilteredBaseCallCount> filteredBccDt = getDataAssemblerFactory()
				.getDataContainerBuilderFactory().filteredBccDt;
		final DataType<FilteredBoolean> filteredBooleanDt = getDataAssemblerFactory()
				.getDataContainerBuilderFactory().filteredBooleanDt;

		return Arrays
				.asList(new HomopolymerFilterFactory(getParameter(), filteredBooleanDt), new ExcludeSiteFilterFactory(),
						new MaxAlleleCountFilterFactory(bccDt),
						new HomozygousFilterFactory(getParameter().getConditionsSize(), bccDt),
						new LRTarrestCombinedFilterFactory(bccDt, filteredBccDt),
						new INDELfilterFactory(bccDt, filteredBccDt), new SpliceSiteFilterFactory(bccDt, filteredBccDt))
				.stream().collect(Collectors.toMap(FilterFactory::getID, Function.identity()));
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> name2resultFormat = new HashMap<>();

		ResultFormat resultFormat = null;

		resultFormat = new BED6lrtArrestResultFormat(getName(), getParameter());
		name2resultFormat.put(resultFormat.getID(), resultFormat);

		return name2resultFormat;
	}

	@Override
	public LRTarrestParameter getParameter() {
		return (LRTarrestParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) { // need at least two conditions
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<ArrestPosition2BaseCallCount> ap2bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().ap2bccDt;

		return Arrays.asList(new MinCoverageValidator(bccDt, getParameter().getConditionParameters()),
				new LRTarrestVariantParallelPileup(ap2bccDt), new ExtendedVariantSiteValidator(bccDt));
	}

	@Override
	public LRTarrestWorker createWorker(final int threadId) {
		return new LRTarrestWorker(this, threadId);
	}

	@Override
	public void debug() {
		AbstractTool.getLogger().addDebug("Add additional column(s) in output start,inner,end!");
	}

	public static class Factory extends AbstractMethodFactory<LRTarrestDataContainerBuilderFactory> {

		public static final String NAME = "lrt-arrest";
		public static final String DESC = "Linkage arrest to base substitution - 2 conditions";

		public Factory() {
			super(NAME, DESC, 2);
		}

		@Override
		public LRTarrestMethod createMethod() {
			final LRTarrestParameter parameter = new LRTarrestParameter(getConditions());
			final LRTarrestDataContainerBuilderFactory builderFactory = new LRTarrestDataContainerBuilderFactory(parameter);

			final LRTarrestDataAssemblerFactory dataAssemblerFactory = new LRTarrestDataAssemblerFactory(
					builderFactory);

			return new LRTarrestMethod(getName(), parameter, dataAssemblerFactory);
		}

		@Override
		public Factory createFactory(int conditions) {
			if (conditions != 2) {
				return null;
			}
			return new Factory();
		}

	}

}
