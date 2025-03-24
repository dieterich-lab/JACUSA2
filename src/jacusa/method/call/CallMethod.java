package jacusa.method.call;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import jacusa.cli.parameters.StatParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.io.format.BED6extendedResultFormat;
import jacusa.io.format.BED6resultFormat;
import jacusa.io.format.call.VCFcallFormat;
import jacusa.io.format.modifyresult.AddBCQC;
import jacusa.io.format.modifyresult.AddDeletionRatio;
import jacusa.io.format.modifyresult.AddInsertionRatio;
import jacusa.io.format.modifyresult.AddReadCount;
import jacusa.io.format.modifyresult.ResultModifier;
import jacusa.io.format.modifyresult.ResultModifierOption;
import jacusa.worker.CallWorker;

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
import lib.cli.options.ShowAllSitesOption;
import lib.cli.options.ShowDeletionCountOption;
import lib.cli.options.ShowInsertionCountOption;
import lib.cli.options.ShowInsertionStartCountOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MaxDepthConditionOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQconditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagConditionOption;
import lib.cli.options.condition.filter.FilterNMsamTagConditionOption;
import lib.data.DataType;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.factory.CallDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.PileupCountBaseCallCountExtractor;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.filter.BooleanData;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.estimate.MinkaParameter;
import lib.data.validator.paralleldata.NonHomozygousSite;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.INDELstat;
import lib.stat.dirmult.CallStat;
import lib.stat.dirmult.DirMultParameter;
import lib.stat.dirmult.DirMultRobustCompoundErrorStatFactory;
import lib.stat.dirmult.ProcessCommandLine;
import lib.stat.sampling.SubSampleStat;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class CallMethod extends AbstractMethod {

	private final Fetcher<BaseCallCount> bccFetcher;
	
	protected CallMethod(
			final String name, 
			final CallParameter parameter, 
			final CallDataAssemblerFactory dataAssemblerFactory) {
		
		super(name, parameter, dataAssemblerFactory);
		bccFetcher = new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
	}
	
	protected Fetcher<BaseCallCount> getBaseCallCountFetcher() {
		return bccFetcher;
	}
	
	protected void initGlobalOptions() {
		addOption(
				new StatFactoryOption(
						getParameter().getStatParameter(), 
						getStatistics()));

		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addOption(
					new ResultFormatOption(
							getParameter(), 
							getResultFormats()));
		}
		
		addOption(new ShowAllSitesOption(getParameter()));
		addOption(new FilterModusOption(getParameter()));
		addOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		addOption(new StatFilterOption(getParameter().getStatParameter()));

		addOption(new ReferenceFastaFilenameOption(getParameter()));
		addOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addOption(new MaxThreadOption(getParameter()));
		addOption(new WindowSizeOption(getParameter()));
		addOption(new ThreadWindowSizeOption(getParameter()));

		addOption(new ShowDeletionCountOption(getParameter()));
		addOption(new ShowInsertionCountOption(getParameter()));
		addOption(new ShowInsertionStartCountOption(getParameter()));
		
		addOption(new BedCoordinatesOption(getParameter()));
		addOption(new ResultFileOption(getParameter()));
		
		addOption(new DebugModusOption(getParameter(), this));
	}
	
	@Override
	protected void initConditionOptions() {
		// for all conditions
		addOption(new MinMAPQconditionOption(getParameter().getConditionParameters()));
		addOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		addOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
		addOption(new MaxDepthConditionOption(getParameter().getConditionParameters()));
		addOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));
		
		addOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters()));
		addOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters()));

		final Set<LibraryType> availableLibType = new HashSet<>(
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		addOption(new nConditionLibraryTypeOption(
				availableLibType, getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
				addOption(new MinMAPQconditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				addOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				addOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				addOption(new MaxDepthConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				addOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				
				addOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				addOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				
				addOption(new nConditionLibraryTypeOption(
						availableLibType,
						getParameter().getConditionParameters().get(conditionIndex),
						getParameter()));
			}
		}
	}
	
	public Map<String, AbstractStatFactory> getStatistics() {
		final Map<String, AbstractStatFactory> statistics = 
				new TreeMap<>();

		AbstractStatFactory statFactory = new DirMultRobustCompoundErrorStatFactory();
		statistics.put(statFactory.getName(), statFactory);

		return statistics;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		return Arrays.asList(
				new ExcludeSiteFilterFactory(),
				new CombinedFilterFactory(
						bccFetcher,
						filteredBccData),
				new INDELfilterFactory(
						bccFetcher, 
						filteredBccData),
				new ReadPositionFilterFactory(
						bccFetcher, 
						filteredBccData),
				new SpliceSiteFilterFactory(
						bccFetcher, 
						filteredBccData),
				new HomozygousFilterFactory(getParameter().getConditionsSize(), bccFetcher),
				new MaxAlleleCountFilterFactory(bccFetcher),
				new HomopolymerFilterFactory(getParameter(), filteredBooleanData))
				.stream()
				.collect(Collectors.toMap(FilterFactory::getID, Function.identity()) );
	}

	public Map<Character, ResultFormat> getResultFormats() {
		final Map<Character, ResultFormat> resultFormats = 
				new HashMap<>();

		ResultFormat resultFormat = null;

		// BED like output
		resultFormat = new BED6resultFormat(getName(), getParameter());
		resultFormats.put(resultFormat.getID(), resultFormat);

		// extended and info expanded output
		final List<ResultModifier> availableResultModifier = Arrays.asList(
				new AddReadCount(),
				new AddBCQC(),
				new AddInsertionRatio(),
				new AddDeletionRatio());
		final List<ResultModifier> selectedResultModifier = new ArrayList<ResultModifier>();
		resultFormat = new BED6extendedResultFormat(
				getName(),
				getParameter(),
				selectedResultModifier,
				new ProcessCommandLine(
						new DefaultParser(),
						availableResultModifier.stream()
							.map(resultModifier -> new ResultModifierOption(resultModifier, selectedResultModifier))
							.collect(Collectors.toList())));
		resultFormats.put(resultFormat.getID(), resultFormat);

		resultFormat = new VCFcallFormat(getParameter());
		resultFormats.put(resultFormat.getID(), resultFormat);

		return resultFormats;
	}

	@Override
	public CallParameter getParameter() {
		return (CallParameter) super.getParameter();
	}

	@Override
	public void parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		super.parseArgs(args);
	}
	
	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		final List<ParallelDataValidator> validators = new ArrayList<ParallelDataValidator>();
		validators.add(new MinCoverageValidator(bccFetcher, getParameter().getConditionParameters()));
		if (! this.getParameter().showAllSites()) {
			validators.add(new NonHomozygousSite(bccFetcher));
		}
		return validators;
	}

	@Override
	public CallWorker createWorker(final int threadId) {
		final double threshold = getParameter().getStatParameter().getThreshold();
		CallStat callStat = (CallStat)getParameter()
				.getStatParameter()
				.getFactory().newInstance(threshold, threadId);
		
		final DirMultParameter dirMultParameter = callStat.getDirMultParameter();
		final MinkaParameter minkaParameter = dirMultParameter.getMinkaEstimateParameter();
		
		final List<INDELstat> indelStats = getINDELstats(minkaParameter);
		
		SubSampleStat subSampleStat = null;
		if (dirMultParameter.getSubsampleRuns() > 0) {
			subSampleStat = new SubSampleStat(dirMultParameter.getSubsampleRuns());
		}

		return new CallWorker(this, threadId, callStat, indelStats, subSampleStat);
	}

	/*
	 * Factory
	 */

	public static class CallBuilderFactory extends AbstractBuilderFactory {

		private CallBuilderFactory(final CallParameter parameter) {
			super(parameter);
		}
		
		@Override
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
		}
		
		@Override
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractMethod.AbstractFactory {

		public static final String NAME_PREFIX = "call-";
		public static final String DESC_PREFIX = "Call variants - ";
		
		public static final int UNKNOWN_CONDITIONS = -1; 
		
		public Factory() {
			this(-1);
		}
		
		private static String getNamePrefix(final int conditions) {
			final StringBuilder sb = new StringBuilder();
			sb.append(NAME_PREFIX);
			if (conditions <= 2) {
				sb.append(conditions);
			} else {
				sb.append("n");
			}
			return sb.toString();
		}
		
		private static String getDescPrefix(final int conditions) {
			final StringBuilder sb = new StringBuilder();
			sb.append(DESC_PREFIX);
			if (conditions == UNKNOWN_CONDITIONS) {
				sb.append('n');
			} else {
				sb.append(conditions);
			}
			if (conditions == 1) {
				sb.append(" condition");
			} else {
				sb.append(" conditions");
			}
			return sb.toString();
		}
		
		public Factory(final int conditions) {
			super(getNamePrefix(conditions), 
					getDescPrefix(conditions),
					conditions);
		}
		
		@Override
		public AbstractMethod createMethod() {
			final CallParameter parameter = new CallParameter(getConditions());
			final CallBuilderFactory builderFactory = new CallBuilderFactory(parameter);
						
			final CallDataAssemblerFactory dataAssemblerFactory = 
					new CallDataAssemblerFactory(builderFactory);
			
			CallMethod callMethod = null;
			switch (getConditions()) {
			case 1:
				callMethod = new OneConditionCallMethod(
						getName(), 
						parameter, 
						dataAssemblerFactory);

			case 2:
				callMethod = new TwoConditionCallMethod(
						getName(), 
						parameter, 
						dataAssemblerFactory);
			}
			if (callMethod == null) {
				throw new IllegalStateException(
						"Arguments could not be parsed check call!");
			}
			
			// set default output and statistic
			// result format
			parameter.setResultFormat(callMethod.getResultFormats().get(BED6resultFormat.CHAR));
			// stat
			parameter.setStatParameter(
					new StatParameter(
							callMethod.getStatistics().get("DirMult"),
							Double.NaN));
			
			return callMethod;
		}
		
		@Override
		public Factory createFactory(int conditions) {
			if (conditions == 1 || conditions == 2) {
				return new Factory(conditions);
			} else {
				return null;
			}
		}
		
	}
	
}
