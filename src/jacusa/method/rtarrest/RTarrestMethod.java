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
import lib.data.fetcher.BaseCallCountAggregator;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.filter.BooleanData;
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

public class RTarrestMethod 
extends AbstractMethod {

	private final Fetcher<BaseCallCount> totalBccAggregator;
	private final Fetcher<BaseCallCount> arrestBccFetcher;
	private final Fetcher<BaseCallCount> throughBccFetcher;
	
	public RTarrestMethod(final String name, 
			final RTarrestParameter parameter, 
			final RTarrestDataAssemblerFactory dataAssemblerFactory) {
		
		super(name, parameter, dataAssemblerFactory);
		arrestBccFetcher 	= DataType.ARREST_BCC.getFetcher();
		throughBccFetcher 	= DataType.THROUGH_BCC.getFetcher();
		totalBccAggregator 	= new BaseCallCountAggregator(
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
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		addACOption(new StatFilterOption(getParameter().getStatParameter()));

		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		// TODO remove addACOption(new StratifyByReadTagOption(getParameter()));
		addACOption(new ShowDeletionCountOption(getParameter()));
		addACOption(new ShowInsertionCountOption(getParameter()));
		addACOption(new ShowInsertionStartCountOption(getParameter()));
		
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
				Arrays.asList(
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		addACOption(new nConditionLibraryTypeOption(
				availableLibType, getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int condI = 0; condI < getParameter().getConditionsSize(); ++condI) {
			addACOption(new MinMAPQConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new MinBASQConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new MinCoverageConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new FilterFlagConditionOption(getParameter().getConditionParameters().get(condI)));
			
			addACOption(new FilterNHsamTagConditionOption(getParameter().getConditionParameters().get(condI)));
			addACOption(new FilterNMsamTagConditionOption(getParameter().getConditionParameters().get(condI)));
			
			addACOption(new nConditionLibraryTypeOption(
					availableLibType, 
					getParameter().getConditionParameters().get(condI),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatFactory> getStats() {
		final Map<String, AbstractStatFactory> factories = 
				new TreeMap<>();

		final List<AbstractStatFactory> tmpFactory = new ArrayList<>(5);
		tmpFactory.add(new RTarrestStatFactory());
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
				.collect(Collectors.toMap(FilterFactory::getID, Function.identity()) );
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> resultFormats = 
				new HashMap<>();

		ResultFormat resultFormat = null;
		resultFormat = new BED6rtArrestResultFormat(
				getName(), 
				getParameter() );
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
			
			/* TODO remove
			if (! parameter.getReadTags().isEmpty()) {
				addBaseSub2bcc(builder, DataType.ARREST_BASE_SUBST);
				addBaseSub2bcc(builder, DataType.THROUGH_BASE_SUBST);
				
				if (parameter.showDeletionCount()) {
					addBaseSub2int(builder, DataType.BASE_SUBST2DELETION_COUNT);
					addBaseSub2int(builder, DataType.BASE_SUBST2COVERAGE);
				}
				if (parameter.showInsertionCount() || parameter.showInsertionStartCount()) {
					addBaseSub2int(builder, DataType.BASE_SUBST2INSERTION_COUNT);
					addBaseSub2int(builder, DataType.BASE_SUBST2COVERAGE);
				}
			}
			*/
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
			
			return new RTarrestMethod(
					getName(),
					parameter,
					dataAssemblerFactory); 
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
