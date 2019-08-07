package jacusa.method.call;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.options.StatFilterOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.io.format.call.BED6callResultFormat;
import jacusa.io.format.call.VCFcallFormat;
import jacusa.worker.CallWorker;

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
import lib.cli.options.CollectReadSubstituionOption;
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
import lib.data.validator.paralleldata.NonHomozygousSite;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.DirMultRobustCompoundErrorStatFactory;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class CallMethod 
extends AbstractMethod {

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
	
	protected void initGlobalACOptions() {
		addACOption(
				new StatFactoryOption(
						getParameter().getStatParameter(), 
						getStatistics()));

		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(
					new ResultFormatOption(
							getParameter(), 
							getResultFormats()));
		}
		
		addACOption(new FilterModusOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		addACOption(new StatFilterOption(getParameter().getStatParameter()));

		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new CollectReadSubstituionOption(getParameter()));
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

		final Set<LibraryType> availableLibType = new HashSet<LibraryType>(
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		addACOption(new nConditionLibraryTypeOption(
				availableLibType, getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
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
	}
	
	public Map<String, AbstractStatFactory> getStatistics() {
		final Map<String, AbstractStatFactory> statistics = 
				new TreeMap<String, AbstractStatFactory>();

		AbstractStatFactory statFactory = null;
		statFactory = new DirMultRobustCompoundErrorStatFactory(
				getParameter().getResultFormat());
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
				new INDEL_FilterFactory(
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
				.collect(Collectors.toMap(FilterFactory::getC, Function.identity()) );
	}

	public Map<Character, ResultFormat> getResultFormats() {
		final Map<Character, ResultFormat> resultFormats = 
				new HashMap<Character, ResultFormat>();

		ResultFormat resultFormat = null;

		// BED like output
		resultFormat = new BED6callResultFormat(getName(), getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);

		resultFormat = new VCFcallFormat(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public CallParameter getParameter() {
		return (CallParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}
	
	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(
				new MinCoverageValidator(bccFetcher, getParameter().getConditionParameters()),
				new NonHomozygousSite(bccFetcher) );
	}

	@Override
	public CallWorker createWorker(final int threadId) {
		return new CallWorker(this, threadId);
	}

	/*
	 * Factory
	 */

	public static class CallBuilderFactory extends AbstractBuilderFactory {

		private final CallParameter parameter;
		
		private CallBuilderFactory(final CallParameter parameter) {
			super(parameter);
			this.parameter = parameter;
		}
		
		@Override
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
			if (parameter.getReadSubstitutions().size() > 0) {
				addBaseSubstitution(builder, DataType.BASE_SUBST2BCC);
			}
			if (parameter.showDeletionCount()) {
				add(builder, DataType.DELETION_COUNT);
				add(builder, DataType.COVERAGE);
			}
			if (parameter.showInsertionCount()) {
				add(builder, DataType.INSERTION_COUNT);
//				add(builder, DataType.COVERAGE);
			}
			if (parameter.getReadSubstitutions().size() > 0 && parameter.showDeletionCount()) {
				addDeletionCount(builder, DataType.BASE_SUBST2DELETION_COUNT);
				addCoverage(builder, DataType.BASE_SUBST2COVERAGE);
			}
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
			sb.append(conditions);
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
			
			switch (getConditions()) {
			case 1:
				return new OneConditionCallMethod(
						getName(), 
						parameter, 
						dataAssemblerFactory);

			case 2:
				return new TwoConditionCallMethod(
						getName(), 
						parameter, 
						dataAssemblerFactory);
				
			default:
				throw new IllegalStateException(
						"Arguments could not be parsed check call!");
			}
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
