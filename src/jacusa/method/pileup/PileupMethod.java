package jacusa.method.pileup;

import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.PileupParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.io.format.pileup.PileupLikeFormat;
import jacusa.worker.PileupWorker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.StratifyByReadTagOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.ShowDeletionCountOption;
import lib.cli.options.ShowInsertionCountOption;
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
import lib.data.assembler.factory.PileupDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.ResultFormat;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class PileupMethod extends AbstractMethod<PileupDataContainerBuilderFactory> {

	protected PileupMethod(final String name, final PileupParameter parameter,
			final PileupDataAssemblerFactory dataAssemblerFactory) {

		super(name, parameter, dataAssemblerFactory);
	}

	protected void initGlobalACOptions() {
		// result format option only if there is a choice
		if (getResultFormats().size() > 1) {
			addACOption(new ResultFormatOption(getParameter(), getResultFormats()));
		}

		addACOption(new FilterModusOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));

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

	@Override
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
				Arrays.asList(LibraryType.UNSTRANDED, LibraryType.RF_FIRSTSTRAND, LibraryType.FR_SECONDSTRAND));

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

	public Map<Character, ResultFormat> getResultFormats() {
		final Map<Character, ResultFormat> outputFormats = new HashMap<>();

		ResultFormat outputFormat = new PileupLikeFormat(getName(), getParameter());
		outputFormats.put(outputFormat.getID(), outputFormat);

		outputFormat = getParameter().getResultFormat();
		outputFormats.put(outputFormat.getID(), outputFormat);

		return outputFormats;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final DataType<BaseCallCount> bccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt;
		final DataType<FilteredBaseCallCount> filteredBccDt = getDataAssemblerFactory().getDataContainerBuilderFactory().filteredBccDt;
		final DataType<FilteredBoolean> filteredBooleanDt = getDataAssemblerFactory().getDataContainerBuilderFactory().filteredBooleanDt;
		
		return Arrays
				.asList(new ExcludeSiteFilterFactory(), new CombinedFilterFactory(bccDt, filteredBccDt),
						new INDELfilterFactory(bccDt, filteredBccDt),
						new ReadPositionFilterFactory(bccDt, filteredBccDt),
						new SpliceSiteFilterFactory(bccDt, filteredBccDt),
						new HomozygousFilterFactory(getParameter().getConditionsSize(), bccDt),
						new MaxAlleleCountFilterFactory(bccDt),
						new HomopolymerFilterFactory(getParameter(), filteredBooleanDt))
				.stream().collect(Collectors.toMap(FilterFactory::getID, Function.identity()));
	}

	@Override
	public PileupParameter getParameter() {
		return (PileupParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	protected String getFiles() {
		return "[OPTIONS] BAM1_1[,BAM1_2,...] [BAM2_1,...] [BAMn_1,...]";
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(new MinCoverageValidator(getDataAssemblerFactory().getDataContainerBuilderFactory().bccDt, getParameter().getConditionParameters()));
	}

	@Override
	public PileupWorker createWorker(final int threadId) {
		return new PileupWorker(this, threadId);
	}

	public static class Factory extends AbstractMethodFactory<PileupDataContainerBuilderFactory> {

		public static final String NAME = "pileup";
		public static final String DESC = "SAMtools like mpileup - 2 conditions";

		public Factory(final int conditions) {
			super(NAME, DESC, conditions);
		}

		@Override
		public PileupMethod createMethod() {
			final PileupParameter parameter = new PileupParameter(getConditions());
			final PileupDataContainerBuilderFactory dataContainerBuilderFactory = new PileupDataContainerBuilderFactory(parameter);

			final PileupDataAssemblerFactory dataAssemblerFactory = new PileupDataAssemblerFactory(dataContainerBuilderFactory);

			return new PileupMethod(getName(), parameter, dataAssemblerFactory);
		}

		@Override
		public Factory createFactory(int conditions) {
			if (conditions == 2) {
				return new Factory(conditions);
			}
			return null;
		}

	}

}
