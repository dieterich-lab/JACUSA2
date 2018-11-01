package jacusa.method.rtarrest;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.distance.rtarrest.RTarrestCombinedFilterFactory;
import jacusa.filter.factory.distance.rtarrest.RTarrestINDEL_FilterFactory;
import jacusa.filter.factory.distance.rtarrest.RTarrestSpliceSiteFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestHomozygousFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestMaxAlleleCountFilterFactory;
import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.worker.RTArrestWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.CollectReadSubstituionOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ShowReferenceOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MaxDepthConditionOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagOption;
import lib.cli.options.condition.filter.FilterNMsamTagOption;
import lib.data.DataType;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.builder.factory.RTarrestDataAssemblerFactory;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.TotalBaseCallCountAggregator;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.data.validator.paralleldata.RTarrestParallelPileup;
import lib.io.ResultFormat;
import lib.method.AbstractMethod;
import lib.stat.AbstractStatFactory;
import lib.util.AbstractTool;
import lib.util.Util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.ParseException;

public class RTarrestMethod 
extends AbstractMethod {

	private final Fetcher<BaseCallCount> totalBccAggregator;
	private final Fetcher<BaseCallCount> arrestBccFetcher;
	private final Fetcher<BaseCallCount> throughBccFetcher;
	
	public RTarrestMethod(final String name, 
			final RTarrestParameter parameter, 
			final RTarrestDataAssemblerFactory dataAssemblerFactory,
			final RTarrestBuilderFactory builderFactory) {
		
		super(name, parameter, dataAssemblerFactory);
		arrestBccFetcher = DataType.ARREST_BCC.getFetcher();
		throughBccFetcher = DataType.THROUGH_BCC.getFetcher();
		totalBccAggregator = new TotalBaseCallCountAggregator(
				Arrays.asList(arrestBccFetcher, throughBccFetcher));
	}

	protected void initGlobalACOptions() {
		addACOption(new StatFactoryOption(
				getParameter().getStatParameter(), getStats()));
		
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption(
					getParameter(), getResultFormats()));
		}
		
		addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		addACOption(new StatFilterOption(getParameter().getStatParameter()));

		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new CollectReadSubstituionOption(getParameter()));
		
		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
		
		addACOption(new DebugModusOption(getParameter(), this));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionLibraryTypeOption(getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionLibraryTypeOption(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatFactory> getStats() {
		final Map<String, AbstractStatFactory> factories = 
				new TreeMap<String, AbstractStatFactory>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<AbstractStatFactory>(5);
		tmpFactory.add(new DummyStatisticFactory());
		tmpFactory.add(new BetaBinFactory());
		
		for (final AbstractStatFactory factory : tmpFactory) {
			factories.put(factory.getName(), factory);
		}
		
		return factories;
	}

	public Map<Character, AbstractFilterFactory> getFilterFactories() {
		final Map<Character, AbstractFilterFactory> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory>();
		
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccFetcher = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanFetcher =
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		final List<AbstractFilterFactory> filterFactories = Arrays.asList(
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
				new HomopolymerFilterFactory(filteredBooleanFetcher) );

		for (final AbstractFilterFactory filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> resultFormats = 
				new HashMap<Character, ResultFormat>();

		ResultFormat resultFormat = null;
		resultFormat = new BED6rtArrestResultFormat(
				getName(), 
				getParameter() );
		resultFormats.put(resultFormat.getC(), resultFormat);
		
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

	public static Builder getReadsOptionBuilder() {
		Set<RT_READS> defaultValues = new HashSet<>();
		for (final RT_READS e : RT_READS.values()) {
			defaultValues.add(e);
		}
		return getReadsOptionBuilder(defaultValues);
	}

	public static Builder getReadsOptionBuilder(final Set<RT_READS> defaultValue) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final RT_READS r : defaultValue) {
			if (! first) {
				sb.append(Util.AND);
				first = false;
			}
			sb.append(r.toString());
		}

		return Option.builder()
				.longOpt("reads")
				.argName("READS")
				.hasArg()
				.desc("Apply filter to base calls from reads: ARREST or THROUGH or ARREST&THROUGH. Default: " + sb.toString());
	}

	public static Set<RT_READS> processApply2Reads(final String line) {
		final Set<RT_READS> apply2reads = new HashSet<RT_READS>(2);
		if (line == null || line.isEmpty()) {
			return apply2reads;
		}
		
		final String[] options = line.toUpperCase().split(Character.toString(Util.AND));
		for (final String option : options) {
			final RT_READS tmpOption = RT_READS.valueOf(option.toUpperCase());
			if (tmpOption == null) {
				throw new IllegalArgumentException("Invalid argument: " + line);						
			}
			apply2reads.add(tmpOption);
		}
		return apply2reads;
	}

	/*
	 * Factory
	 */
	
	public static class RTarrestBuilderFactory extends AbstractBuilderFactory {

		private final RTarrestParameter parameter;
		
		private RTarrestBuilderFactory(final RTarrestParameter parameter) {
			super(parameter);
			this.parameter = parameter;
		}
		
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.ARREST_BCC);
			add(builder, DataType.THROUGH_BCC);
			if (parameter.getReadSubstitutions() != null) {
				add(builder, DataType.BASE_SUBST);
			}
		}
		
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractMethod.AbstractFactory {

		public final static String NAME = "rt-arrest";
		public final static String DESC = "Reverse Transcription Arrest - 2 conditions";

		public Factory() {
			super(NAME, DESC, 2);
		}
		
		@Override
		public AbstractMethod createMethod() {
			final RTarrestParameter parameter = new RTarrestParameter(getConditions());
			final RTarrestBuilderFactory builderFactory = new RTarrestBuilderFactory(parameter);
			
			final RTarrestDataAssemblerFactory dataAssemblerFactory = 
					new RTarrestDataAssemblerFactory(builderFactory);
			
			return new RTarrestMethod(
					getName(),
					parameter,
					dataAssemblerFactory,
					builderFactory); 
		}
		
		@Override
		public Factory createFactory(int conditions) {
			if (conditions != 2) {
				return null;
				// throw new IllegalArgumentException("Number of conditions not supported: " + conditions);
			}
			 
			return new Factory();
		}
		
	}
	
}
