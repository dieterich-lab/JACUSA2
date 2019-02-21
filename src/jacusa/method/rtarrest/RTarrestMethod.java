package jacusa.method.rtarrest;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestCombinedFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestINDEL_FilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestSpliceSiteFilterFactory;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;

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
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
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
import lib.data.fetcher.TotalBaseCallCountAggregator;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.data.validator.paralleldata.RTarrestParallelPileup;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.betabin.RTarrestStatFactory;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;

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
		arrestBccFetcher 	= DataType.ARREST_BCC.getFetcher();
		throughBccFetcher 	= DataType.THROUGH_BCC.getFetcher();
		totalBccAggregator 	= new TotalBaseCallCountAggregator(
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
		// not needed addACOption(new MaxDepthConditionOption(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters()));
		
		addACOption(new nConditionLibraryTypeOption(getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			// not needed addACOption(new MaxDepthConditionOption(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new nConditionLibraryTypeOption(
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatFactory> getStats() {
		final Map<String, AbstractStatFactory> factories = 
				new TreeMap<String, AbstractStatFactory>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<AbstractStatFactory>(5);
		// tmpFactory.add(new DummyStatisticFactory());
		tmpFactory.add(new RTarrestStatFactory());
		for (final AbstractStatFactory factory : tmpFactory) {
			factories.put(factory.getName(), factory);
		}
		return factories;
	}

	public Map<Character, FilterFactory> getFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccFetcher = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanFetcher =
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		return Arrays.asList(
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
				.collect(Collectors.toMap(FilterFactory::getC, Function.identity()) );
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
			if (parameter.getReadSubstitutions().size() > 0) {
				addBaseSubstitution(builder, DataType.ARREST_BASE_SUBST);
				addBaseSubstitution(builder, DataType.THROUGH_BASE_SUBST);
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
