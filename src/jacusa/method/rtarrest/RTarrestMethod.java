package jacusa.method.rtarrest;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestCombinedFilterFactory;
import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.worker.RTArrestWorker;

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
import lib.cli.options.StratifyByReadTagOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.ShowDeletionCountOption;
import lib.cli.options.ShowInsertionCountOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagConditionOption;
import lib.cli.options.condition.filter.FilterNMsamTagConditionOption;
import lib.data.DataType;
import lib.data.assembler.factory.RTarrestDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.data.validator.paralleldata.RTarrestParallelPileup;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.betabin.RTarrestStatFactory;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class RTarrestMethod extends AbstractMethod<RTarrestDataContainerBuilderFactory> {

	public RTarrestMethod(final String name, final RTarrestParameter parameter,
			final RTarrestDataAssemblerFactory dataAssemblerFactory) {

		super(name, parameter, dataAssemblerFactory);
	}

	protected void initGlobalACOptions() {
		addACOption(new StatFactoryOption(getParameter().getStatParameter(), getStats()));

		// result format option only if there is a choice
		if (getResultFormats().size() > 1) {
			addACOption(new ResultFormatOption(getParameter(), getResultFormats()));
		}

		addACOption(new FilterModusOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));

		addACOption(new StatFilterOption(getParameter().getStatParameter()));

		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new StratifyByReadTagOption(getParameter()));
		addACOption(new ShowDeletionCountOption(getParameter()));
		addACOption(new ShowInsertionCountOption(getParameter()));

		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));

		addACOption(new DebugModusOption(getParameter(), this));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
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
			addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(condI)));

			addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(condI)));

			addACOption(new nConditionLibraryTypeOption(availableLibType,
					getParameter().getConditionParameters().get(condI), getParameter()));
		}
	}

	public Map<String, AbstractStatFactory> getStats() {
		final Map<String, AbstractStatFactory> factories = new TreeMap<>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<>(5);
		tmpFactory.add(new RTarrestStatFactory());
		for (final AbstractStatFactory factory : tmpFactory) {
			factories.put(factory.getName(), factory);
		}
		return factories;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<FilteredBaseCallCount> filteredBccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().filteredBccDt;
		final DataType<FilteredBoolean> filteredBooleanDt = getDataAssemblerFactory().getDataContainerBuilderFactory().filteredBooleanDt;

		return Arrays
				.asList(new ExcludeSiteFilterFactory(),
						new RTarrestCombinedFilterFactory(bccDt, filteredBccDt),
						new INDELfilterFactory(bccDt, filteredBccDt),
						new SpliceSiteFilterFactory(bccDt, filteredBccDt),
						new HomozygousFilterFactory(getParameter().getConditionsSize(), bccDt),
						new MaxAlleleCountFilterFactory(bccDt),
						new HomopolymerFilterFactory(getParameter(), filteredBooleanDt))
				.stream().collect(Collectors.toMap(FilterFactory::getID, Function.identity()));
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> resultFormats = new HashMap<>();

		ResultFormat resultFormat = null;
		resultFormat = new BED6rtArrestResultFormat(getName(), getParameter());
		resultFormats.put(resultFormat.getID(), resultFormat);

		return resultFormats;
	}

	@Override
	public RTarrestParameter getParameter() {
		return (RTarrestParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<BaseCallCount> arrestBccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().arrestBccDt;
		final DataType<BaseCallCount> throughBccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().throughBccDt;
		
		return Arrays.asList(new MinCoverageValidator(bccDt, getParameter().getConditionParameters()),
				new RTarrestParallelPileup(bccDt, arrestBccDt, throughBccDt));
	}

	@Override
	public RTArrestWorker createWorker(final int threadId) {
		return new RTArrestWorker(this, threadId);
	}

	@Override
	public void debug() {
		// set custom
		AbstractTool.getLogger().addDebug("Add additional column(s) in output start,inner,end!");
	}

	public enum RT_READS {
		ARREST, THROUGH
	}

	/*
	 * Factory
	 */

	public static class Factory extends AbstractMethodFactory<RTarrestDataContainerBuilderFactory> {

		public static final String NAME = "rt-arrest";
		public static final String DESC = "Reverse Transcription Arrest - 2 conditions";

		public Factory() {
			super(NAME, DESC, 2);
		}

		@Override
		public AbstractMethod<RTarrestDataContainerBuilderFactory> createMethod() {
			final RTarrestParameter parameter = new RTarrestParameter(getConditions());
			final RTarrestDataContainerBuilderFactory builderFactory = new RTarrestDataContainerBuilderFactory(parameter);

			final RTarrestDataAssemblerFactory dataAssemblerFactory = new RTarrestDataAssemblerFactory(builderFactory);

			return new RTarrestMethod(getName(), parameter, dataAssemblerFactory);
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
