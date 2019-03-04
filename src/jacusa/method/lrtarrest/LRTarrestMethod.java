package jacusa.method.lrtarrest;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestCombinedFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestINDEL_FilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.LRTarrestSpliceSiteFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestHomozygousFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestMaxAlleleCountFilterFactory;
import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat;
import jacusa.method.rtarrest.DummyStatisticFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
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
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
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

public class LRTarrestMethod 
extends AbstractMethod {

	private final Fetcher<ArrestPosition2baseCallCount> ap2bccFetcher;
	private final Fetcher<BaseCallCount> totalBccFetcher;
	private final Fetcher<BaseCallCount> arrestBccExtractor;
	private final Fetcher<BaseCallCount> throughBccExtractor;
	
	private LRTarrestMethod(
			final String name,
			final LRTarrestParameter parameter,
			final LRTarrestDataAssemblerFactory dataAssemblerFactory,
			final LRTarrestBuilderFactory builderFactory) {
		
		super(name, parameter, dataAssemblerFactory);
		ap2bccFetcher 		= DataType.AP2BCC.getFetcher();
		totalBccFetcher 	= new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
		arrestBccExtractor 	= new ArrestBaseCallCountExtractor(ap2bccFetcher);
		throughBccExtractor = new ThroughBaseCallCountExtractor(ap2bccFetcher);
	}

	public Fetcher<BaseCallCount> getTotalBaseCallCountFetcher() {
		return totalBccFetcher;
	}
	
	protected void initGlobalACOptions() {
		addACOption(new StatFactoryOption(getParameter().getStatParameter(), getStatistics()));
		
		addACOption(new StatFilterOption(getParameter().getStatParameter()));
		
		// result format option only if there is a choice		
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption(
					getParameter(), getResultFormats()));
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
		
		final Set<LibraryType> availableLibType = new HashSet<LibraryType>(
				Arrays.asList(
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		addACOption(new nConditionLibraryTypeOption(
				availableLibType, 
				getParameter().getConditionParameters(), 
				getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new nConditionLibraryTypeOption(
					availableLibType, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatFactory> getStatistics() {
		final Map<String, AbstractStatFactory> factories = 
				new TreeMap<String, AbstractStatFactory>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<AbstractStatFactory>(5);
		tmpFactory.add(new DummyStatisticFactory());
		tmpFactory.add(new LRTarrestStatFactory());

		for (final AbstractStatFactory factory : tmpFactory) {
			factories.put(factory.getName(), factory);
		}
		
		return factories;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccFetcher = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanFetcher =
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);

		return Arrays.asList(
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
				new LRTarrestINDEL_FilterFactory(
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
				.collect(Collectors.toMap(FilterFactory::getC, Function.identity()) );
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> name2resultFormat = 
				new HashMap<Character, ResultFormat>();

		ResultFormat resultFormat = null;
		
		resultFormat = new BED6lrtArrestResultFormat(
				getName(), 
				getParameter() );
		name2resultFormat.put(resultFormat.getC(), resultFormat);
		
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
		
		public final static String NAME = "lrt-arrest";
		public final static String DESC = "Linkage arrest to base substitution - 2 conditions";
		
		public Factory() {
			super(NAME, DESC, 2);
		}

		@Override
		public AbstractMethod createMethod() {
			final LRTarrestParameter parameter = new LRTarrestParameter(getConditions());
			final LRTarrestBuilderFactory builderFactory = new LRTarrestBuilderFactory(parameter);
			
			final LRTarrestDataAssemblerFactory dataAssemblerFactory = 
					new LRTarrestDataAssemblerFactory(builderFactory);
			
			return new LRTarrestMethod(
					getName(),
					parameter,
					dataAssemblerFactory,
					builderFactory);
		}

		
		@Override
		public AbstractFactory createFactory(int conditions) {
			if (conditions != 2) {
				// throw new IllegalArgumentException("Number of conditions not supported: " + conditions);
				return null;
			}
			return new Factory();
		}
				
	}
	
}
