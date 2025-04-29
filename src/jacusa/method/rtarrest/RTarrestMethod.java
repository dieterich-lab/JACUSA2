package jacusa.method.rtarrest;

import jacusa.cli.options.StatFactoryOption;

import jacusa.cli.options.ThresholdFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.cli.parameters.StatParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestCombinedFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestINDEL_FilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestSpliceSiteFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestHomozygousFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestMaxAlleleCountFilterFactory;
import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.worker.RTArrestWorker;

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
import lib.cli.options.ShowDeletionCountOption;
import lib.cli.options.ShowInsertionCountOption;
import lib.cli.options.ShowInsertionStartCountOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQconditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagConditionOption;
import lib.cli.options.condition.filter.FilterNMsamTagConditionOption;
import lib.data.DataType;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.assembler.factory.RTarrestDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.BaseCallCountAggregator;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.filter.BooleanData;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.data.validator.paralleldata.RTarrestParallelPileup;
import lib.stat.betabin.RTarrestStatFactory;
import lib.stat.dirmult.ProcessCommandLine;
import lib.stat.dirmult.options.EpsilonOptions;
import lib.stat.dirmult.options.MaxIterationsOption;
import lib.stat.dirmult.options.ShowAlphaOption;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class RTarrestMethod 
extends AbstractMethod {

	private final RTarrestParameter parameter;
	
	private final Fetcher<BaseCallCount> totalBccAggregator;
	private final Fetcher<BaseCallCount> arrestBccFetcher;
	private final Fetcher<BaseCallCount> throughBccFetcher;
	
	public RTarrestMethod(
			final String name, 
			final RTarrestParameter parameter,
			final RTarrestDataAssemblerFactory dataAssemblerFactory) {
		super(name, dataAssemblerFactory);
		
		this.parameter 		= parameter;

		arrestBccFetcher 	= DataType.ARREST_BCC.getFetcher();
		throughBccFetcher 	= DataType.THROUGH_BCC.getFetcher();
		totalBccAggregator 	= new BaseCallCountAggregator(
				Arrays.asList(arrestBccFetcher, throughBccFetcher));
	}

	protected void registerGlobalOptions() {
		registerOption(new StatFactoryOption(
				getParameter().getStatParameter(), getStatisticFactories()));
		
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			registerOption(new ResultFormatOption(
					getParameter(), getResultFormats()));
		}
		
		registerOption(new FilterModusOption(getParameter()));
		registerOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		registerOption(new ThresholdFilterOption(getParameter().getStatParameter()));

		registerOption(new ReferenceFastaFilenameOption(getParameter()));
		registerOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		registerOption(new MaxThreadOption(getParameter()));
		registerOption(new WindowSizeOption(getParameter()));
		registerOption(new ThreadWindowSizeOption(getParameter()));

		registerOption(new ShowDeletionCountOption(getParameter(),
				new ProcessCommandLine(new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(getParameter().getDeletionEstimationParameter().getMinkaParameter()),
								new ShowAlphaOption(getParameter().getDeletionEstimationParameter()),
								new MaxIterationsOption(getParameter().getDeletionEstimationParameter().getMinkaParameter())))));
		registerOption(new ShowInsertionCountOption(getParameter(),
				new ProcessCommandLine(new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(getParameter().getInsertionEstimationParameter().getMinkaParameter()),
								new ShowAlphaOption(getParameter().getInsertionEstimationParameter()),
								new MaxIterationsOption(getParameter().getInsertionEstimationParameter().getMinkaParameter())))));				
		registerOption(new ShowInsertionStartCountOption(getParameter(),
				new ProcessCommandLine(new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(getParameter().getInsertionEstimationParameter().getMinkaParameter()),
								new ShowAlphaOption(getParameter().getInsertionEstimationParameter()),
								new MaxIterationsOption(getParameter().getInsertionEstimationParameter().getMinkaParameter())))));
		
		registerOption(new BedCoordinatesOption(getParameter()));
		registerOption(new ResultFileOption(getParameter()));
		
		registerOption(new DebugModusOption(getParameter(), this));
	}

	protected void registerConditionOptions() {
		// for all conditions
		registerOption(new MinMAPQconditionOption(getParameter().getConditionParameters()));
		registerOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		registerOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
		registerOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));
		
		registerOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters()));
		registerOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters()));
		
		final Set<LibraryType> availableLibType = new HashSet<>(
				Arrays.asList(
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		registerOption(new nConditionLibraryTypeOption(
				availableLibType, getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			registerOption(new MinMAPQconditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			registerOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			registerOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			registerOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			registerOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			registerOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			registerOption(new nConditionLibraryTypeOption(
					availableLibType, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public void registerStatisticFactories() {
		registerStatisticFactory(
				new RTarrestStatFactory(
						getParameter().getBetaBinParameter()));
	}

	public void registerFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccFetcher = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanFetcher =
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		Arrays.asList(
				new ExcludeSiteFilterFactory(),
				new RTarrestCombinedFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccAggregator, 
								arrestBccFetcher, 
								throughBccFetcher),
						filteredBccFetcher),
				new RTarrestINDEL_FilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccAggregator, 
								arrestBccFetcher, 
								throughBccFetcher),
						filteredBccFetcher),
				new RTarrestSpliceSiteFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccAggregator, 
								arrestBccFetcher, 
								throughBccFetcher),
							filteredBccFetcher),
				new RTarrestHomozygousFilterFactory(
						getParameter().getConditionsSize(),
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccAggregator, 
								arrestBccFetcher, 
								throughBccFetcher)),
				new RTarrestMaxAlleleCountFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccAggregator, 
								arrestBccFetcher, 
								throughBccFetcher)),
				new HomopolymerFilterFactory(getParameter(), filteredBooleanFetcher))
				.stream()
				.forEach(f -> registerFilterFactory(f));
	}

	public void registerResultFormats() {
		registerResultFormat(
				new BED6rtArrestResultFormat(
						getName(), 
						getParameter()));
	}

	@Override
	public RTarrestParameter getParameter() {
		return parameter;
	}

	@Override
	public void parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(
				new MinCoverageValidator(totalBccAggregator, getParameter().getConditionParameters()),
				new RTarrestParallelPileup(
						totalBccAggregator,
						arrestBccFetcher,
						throughBccFetcher) );
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
		ARREST,
		THROUGH
	}

	/*
	 * Factory
	 */
	
	public static class RTarrestBuilderFactory extends AbstractBuilderFactory {

		private RTarrestBuilderFactory(final RTarrestParameter parameter) {
			super(parameter);
		}
		
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.ARREST_BCC);
			add(builder, DataType.THROUGH_BCC);
		}
		
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractMethod.AbstractFactory {

		public static final String NAME = "rt-arrest";
		public static final String DESC = "Reverse Transcription Arrest - 2 conditions";

		public Factory() {
			super(NAME, DESC, 2);
		}
		
		@Override
		public AbstractMethod createMethod() {
			final RTarrestParameter parameter = new RTarrestParameter(getConditions());
			final RTarrestBuilderFactory builderFactory = new RTarrestBuilderFactory(parameter);
			
			final RTarrestDataAssemblerFactory dataAssemblerFactory = 
					new RTarrestDataAssemblerFactory(builderFactory);
			
			final RTarrestMethod method = new RTarrestMethod(
					getName(),
					parameter,
					dataAssemblerFactory);
			
			// related to test-statistic
			parameter.setStatParameter(
					new StatParameter(
							method.getStatisticFactories().get(RTarrestStatFactory.NAME),
							Double.NaN));
			// default result format
			parameter.setResultFormat(
					method.getResultFormats().get(BED6rtArrestResultFormat.CHAR));
			
			return method;
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
