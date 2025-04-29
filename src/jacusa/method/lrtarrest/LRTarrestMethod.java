package jacusa.method.lrtarrest;

import jacusa.cli.options.StatFactoryOption;

import jacusa.cli.options.ThresholdFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.cli.parameters.StatParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestCombinedFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestINDELfilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestSpliceSiteFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestHomozygousFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestMaxAlleleCountFilterFactory;
import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat;
import jacusa.method.rtarrest.DummyStatisticFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import jacusa.worker.LRTarrestWorker;

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
import lib.data.assembler.factory.LRTarrestDataAssemblerFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.fetcher.basecall.ArrestBaseCallCountExtractor;
import lib.data.fetcher.basecall.PileupCountBaseCallCountExtractor;
import lib.data.fetcher.basecall.ThroughBaseCallCountExtractor;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanData;
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.lrtarrest.ArrestPos2BCC;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.LRTarrestVariantParallelPileup;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.stat.betabin.LRTarrestStatFactory;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class LRTarrestMethod 
extends AbstractMethod {

	private final LRTarrestParameter parameter;
	
	private final Fetcher<ArrestPos2BCC> ap2bccFetcher;
	private final Fetcher<BaseCallCount> totalBccFetcher;
	private final Fetcher<BaseCallCount> arrestBccExtractor;
	private final Fetcher<BaseCallCount> throughBccExtractor;
	
	private LRTarrestMethod(
			final String name,
			final LRTarrestParameter parameter,
			final LRTarrestDataAssemblerFactory dataAssemblerFactory) {
		super(name, dataAssemblerFactory);
		this.parameter		= parameter;
		
		ap2bccFetcher 		= DataType.AP2BCC.getFetcher();
		totalBccFetcher 	= new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
		arrestBccExtractor 	= new ArrestBaseCallCountExtractor(ap2bccFetcher);
		throughBccExtractor = new ThroughBaseCallCountExtractor(ap2bccFetcher);
	}

	public Fetcher<BaseCallCount> getTotalBaseCallCountFetcher() {
		return totalBccFetcher;
	}
	
	protected void registerGlobalOptions() {
		registerOption(new StatFactoryOption(getParameter().getStatParameter(), getStatisticFactories()));
		
		registerOption(new ThresholdFilterOption(getParameter().getStatParameter()));
		
		// result format option only if there is a choice		
		if (getResultFormats().size() > 1 ) {
			registerOption(new ResultFormatOption(
					getParameter(), getResultFormats()));
		}
		
		registerOption(new FilterModusOption(getParameter()));
		registerOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		registerOption(new ReferenceFastaFilenameOption(getParameter()));

		registerOption(new MaxThreadOption(getParameter()));
		registerOption(new WindowSizeOption(getParameter()));
		registerOption(new ThreadWindowSizeOption(getParameter()));

		registerOption(new BedCoordinatesOption(getParameter()));
		registerOption(new ResultFileOption(getParameter()));
		
		registerOption(new DebugModusOption(getParameter(), this));
		registerOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
	}

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
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		registerOption(new nConditionLibraryTypeOption(
				availableLibType, 
				getParameter().getConditionParameters(), 
				getParameter()));
		
		// condition specific
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
	
	public void registerStatisticFactories() {
		registerStatisticFactory(new DummyStatisticFactory());
		registerStatisticFactory(new LRTarrestStatFactory(getParameter().getLRTarrestBetaBinParameter()));
	}

	public void registerFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccFetcher = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanFetcher =
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);

		Arrays.asList(
				new HomopolymerFilterFactory(getParameter(), filteredBooleanFetcher),
				new ExcludeSiteFilterFactory(),
				new RTarrestMaxAlleleCountFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccFetcher, 
								arrestBccExtractor, 
								throughBccExtractor)),
				new RTarrestHomozygousFilterFactory(
						getParameter().getConditionsSize(),
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccFetcher, 
								arrestBccExtractor, 
								throughBccExtractor)),
				new LRTarrestCombinedFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccFetcher, 
								arrestBccExtractor, 
								throughBccExtractor),
						filteredBccFetcher),
				new LRTarrestINDELfilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccFetcher, 
								arrestBccExtractor, 
								throughBccExtractor),
						filteredBccFetcher),
				new LRTarrestSpliceSiteFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								totalBccFetcher, 
								arrestBccExtractor, 
								throughBccExtractor),
						filteredBccFetcher))
				.stream()
				.forEach(f -> registerFilterFactory(f));
	}

	public void registerResultFormats() {
		registerResultFormat(
				new BED6lrtArrestResultFormat(
						getName(), 
						getParameter()));
	}

	@Override
	public LRTarrestParameter getParameter() {
		return parameter;
	}

	@Override
	public void parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) { // need at least two conditions
			throw new ParseException("BAM File is not provided!");
		}

		super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(
				new MinCoverageValidator(totalBccFetcher,
						getParameter().getConditionParameters()),
				new LRTarrestVariantParallelPileup(ap2bccFetcher),
				new ExtendedVariantSiteValidator(totalBccFetcher));
	}
	
	@Override
	public LRTarrestWorker createWorker(final int threadId) {
		return new LRTarrestWorker(this, threadId);
	}
	
	@Override
	public void debug() {
		AbstractTool.getLogger().addDebug("Add additional column(s) in output start,inner,end!");
	}

	/*
	 * Factory
	 */
	
	public static class LRTarrestBuilderFactory extends AbstractBuilderFactory {
		
		private LRTarrestBuilderFactory(final LRTarrestParameter parameter) {
			super(parameter);
		}
		
		@Override
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
			add(builder, DataType.AP2BCC);
		}
		
		@Override
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractFactory {
		
		public static final String NAME = "lrt-arrest";
		public static final String DESC = "Linkage arrest to base substitution - 2 conditions";
		
		public Factory() {
			super(NAME, DESC, 2);
		}

		@Override
		public AbstractMethod createMethod() {
			final LRTarrestParameter parameter = new LRTarrestParameter(getConditions());
			final LRTarrestBuilderFactory builderFactory = new LRTarrestBuilderFactory(parameter);
			
			final LRTarrestDataAssemblerFactory dataAssemblerFactory = 
					new LRTarrestDataAssemblerFactory(builderFactory);

			final LRTarrestMethod method = new LRTarrestMethod(
					getName(),
					parameter,
					dataAssemblerFactory);
			
			// test-statistic related
			parameter.setStatParameter(
					new StatParameter(
							method.getStatisticFactories().get(LRTarrestStatFactory.NAME),
							Double.NaN));
			
			// default output format
			parameter.setResultFormat(
					method.getResultFormats().get(BED6lrtArrestResultFormat.CHAR));
			
			return method;
		}

		
		@Override
		public AbstractFactory createFactory(int conditions) {
			if (conditions != 2) {
				return null;
			}
			return new Factory();
		}
				
	}
	
}
