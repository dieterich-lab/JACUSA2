package jacusa.method.call;

import jacusa.cli.options.StatFactoryOption;

import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import jacusa.cli.parameters.StatParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
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
import jacusa.worker.CallWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import lib.stat.INDELstat;
import lib.stat.dirmult.CallStat;
import lib.stat.dirmult.DirMultParameter;
import lib.stat.dirmult.DirMultRobustCompoundErrorStatFactory;
import lib.stat.sampling.SubSampleStat;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class CallMethod extends AbstractMethod {

	private final CallParameter parameter;
	
	private final Fetcher<BaseCallCount> bccFetcher;
	
	protected CallMethod(
			final String name, 
			final CallParameter parameter,
			final CallDataAssemblerFactory dataAssemblerFactory) {
		super(name, dataAssemblerFactory);
		
		this.parameter = parameter;
		bccFetcher = new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
	}
	
	@Override
	public CallParameter getParameter() {
		return parameter;
	}
	
	protected Fetcher<BaseCallCount> getBaseCallCountFetcher() {
		return bccFetcher;
	}
	
	protected void registerGlobalOptions() {
		registerOption(
				new StatFactoryOption(
						getParameter().getStatParameter(), 
						getStatisticFactories()));

		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			registerOption(
					new ResultFormatOption(
							getParameter(), 
							getResultFormats()));
		}
		
		registerOption(new ShowAllSitesOption(getParameter()));
		registerOption(new FilterModusOption(getParameter()));
		registerOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		registerOption(new StatFilterOption(getParameter().getStatParameter()));

		registerOption(new ReferenceFastaFilenameOption(getParameter()));
		registerOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		registerOption(new MaxThreadOption(getParameter()));
		registerOption(new WindowSizeOption(getParameter()));
		registerOption(new ThreadWindowSizeOption(getParameter()));

		registerOption(new ShowDeletionCountOption(getParameter()));
		registerOption(new ShowInsertionCountOption(getParameter()));
		registerOption(new ShowInsertionStartCountOption(getParameter()));
		
		registerOption(new BedCoordinatesOption(getParameter()));
		registerOption(new ResultFileOption(getParameter()));
		
		registerOption(new DebugModusOption(getParameter(), this));
	}
	
	@Override
	protected void registerConditionOptions() {
		// for all conditions
		registerOption(new MinMAPQconditionOption(getParameter().getConditionParameters()));
		registerOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		registerOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
		registerOption(new MaxDepthConditionOption(getParameter().getConditionParameters()));
		registerOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));
		
		registerOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters()));
		registerOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters()));

		final Set<LibraryType> availableLibType = new HashSet<>(
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		registerOption(new nConditionLibraryTypeOption(
				availableLibType, getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
				registerOption(new MinMAPQconditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				registerOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				registerOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				registerOption(new MaxDepthConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				registerOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				
				registerOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				registerOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
				
				registerOption(new nConditionLibraryTypeOption(
						availableLibType,
						getParameter().getConditionParameters().get(conditionIndex),
						getParameter()));
			}
		}
	}
	
	@Override
	public void registerStatisticFactories() {
		registerStatisticFactory(
				new DirMultRobustCompoundErrorStatFactory(
						getParameter().getDirMultParameter()));
	}

	public void registerFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		registerFilterFactory(new ExcludeSiteFilterFactory());
		registerFilterFactory(
				new CombinedFilterFactory(
						bccFetcher,
						filteredBccData));
		registerFilterFactory(
				new INDELfilterFactory(
						bccFetcher, 
						filteredBccData));
		registerFilterFactory(
				new ReadPositionFilterFactory(
						bccFetcher, 
						filteredBccData));
		registerFilterFactory(
				new SpliceSiteFilterFactory(
						bccFetcher, 
						filteredBccData));
		registerFilterFactory(
				new HomozygousFilterFactory(
						getParameter().getConditionsSize(),
						bccFetcher));
		registerFilterFactory(new MaxAlleleCountFilterFactory(bccFetcher));
		registerFilterFactory(new HomopolymerFilterFactory(getParameter(), filteredBooleanData));
	}

	public void registerResultFormats() {
		// BED like output
		registerResultFormat(new BED6resultFormat(getName(), getParameter()));
		registerResultFormat(new BED6extendedResultFormat(getName(), getParameter()));
		registerResultFormat(new VCFcallFormat(getParameter()));
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
				.getFactory().newInstance(threshold, getParameter().getConditionsSize());
		
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
							callMethod.getStatisticFactories().get(DirMultRobustCompoundErrorStatFactory.NAME),
							Double.NaN));
			
			return callMethod;
		}
		
		@Override
		public Factory createFactory(int conditions) {
			if (conditions == 1 || conditions == 2) {
				return new Factory(conditions);
			} else {
				throw new IllegalArgumentException("Only condition: 1 or 2 are supported!"); 
			}
		}
		
	}
	
}
