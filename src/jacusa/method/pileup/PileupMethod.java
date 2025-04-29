package jacusa.method.pileup;

import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;

import jacusa.cli.parameters.PileupParameter;
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
import jacusa.io.format.pileup.BED6pileupResultFormat;
import jacusa.io.format.pileup.PileupLikeFormat;
import jacusa.method.rtarrest.CoverageStatisticFactory;
import jacusa.worker.PileupWorker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.cli.options.BedCoordinatesOption;
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
import lib.cli.options.ShowInsertionStartCountOption;
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
import lib.data.assembler.factory.PileupDataAssemblerFactory;
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
import lib.stat.AbstractStat;
import lib.stat.dirmult.ProcessCommandLine;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.LibraryType;

import org.apache.commons.cli.ParseException;

public class PileupMethod 
extends AbstractMethod {
	
	private final PileupParameter parameter;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	protected PileupMethod(
			final String name, 
			final PileupParameter parameter,
			final PileupDataAssemblerFactory dataAssemblerFactory) {
		super(name, dataAssemblerFactory);
		
		this.parameter = parameter;
		bccFetcher = new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
	}
	
	protected void registerGlobalOptions() {
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			registerOption(new ResultFormatOption(getParameter(), getResultFormats()));
		}

		registerOption(new FilterModusOption(getParameter()));
		registerOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
		registerOption(new ReferenceFastaFilenameOption(getParameter()));
		registerOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		registerOption(new MaxThreadOption(getParameter()));
		registerOption(new WindowSizeOption(getParameter()));
		registerOption(new ThreadWindowSizeOption(getParameter()));
		
		registerOption(new ShowDeletionCountOption(getParameter(), new ProcessCommandLine()));
		registerOption(new ShowInsertionCountOption(getParameter(), new ProcessCommandLine()));				
		registerOption(new ShowInsertionStartCountOption(getParameter(), new ProcessCommandLine()));
		
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

	@Override
	public void registerStatisticFactories() {
		// nothing to be done
	}
	
	public void registerResultFormats() {
		registerResultFormat(new PileupLikeFormat(getName(), getParameter()));

		// extended and info expanded output
		registerResultFormat(new BED6extendedResultFormat(getName(), getParameter()));

		registerResultFormat(new BED6pileupResultFormat(getName(), getParameter()));
	}

	public void registerFilterFactories() {
		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		Arrays.asList(
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
				.forEach(filterFactor -> registerFilterFactory(filterFactor));
	}

	@Override
	public PileupParameter getParameter() {
		return parameter;
	}
	
	@Override
	public void parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		super.parseArgs(args); 
	}

	@Override
	protected String getFiles() {
		return "[OPTIONS] BAM1_1[,BAM1_2,...] [BAM2_1,...] [BAMn_1,...]";
	}
	
	@Override
	public List<ParallelDataValidator> createParallelDataValidators() {
		return Arrays.asList(
				new MinCoverageValidator(bccFetcher, getParameter().getConditionParameters()));
	}
	
	@Override
	public PileupWorker createWorker(final int threadId) {
		final double threshold = getParameter().getStatParameter().getThreshold();
		AbstractStat stat = getParameter()
				.getStatParameter()
				.getFactory().newInstance(threshold, threadId);
		
		final MinkaParameter minkaParameters = new MinkaParameter(); 
		return new PileupWorker(this, threadId, stat, getINDELstats(minkaParameters));
	}
	
	/*
	 * Factory
	 */
	
	public static class PileupBuilderFactory extends AbstractBuilderFactory {

		public PileupBuilderFactory(final PileupParameter parameter) {
			super(parameter);
		}
		
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
		}
		
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractMethod.AbstractFactory {
	
		public static final String NAME = "pileup";
		public static final String DESC = "SAMtools like mpileup - n conditions";
		
		public Factory(final int conditions) {
			super(NAME, DESC, conditions);
		}
		
		@Override
		public PileupMethod createMethod() {
			final PileupParameter parameter 			= new PileupParameter(getConditions());
			final PileupBuilderFactory builderFactory 	= new PileupBuilderFactory(parameter);
			
			final PileupDataAssemblerFactory dataAssemblerFactory = 
					new PileupDataAssemblerFactory(builderFactory);
			
			final PileupMethod method = new PileupMethod(
					getName(),
					parameter,
					dataAssemblerFactory);
			
			// set defaults move to parameter
			parameter.setStatParameter(new StatParameter(new CoverageStatisticFactory(), Double.NaN));
			parameter.setResultFormat(method.getResultFormats().get(BED6pileupResultFormat.CHAR));
						
			return method;
		}
		
		@Override
		public Factory createFactory(int conditions) {
			return new Factory(conditions);
		}
		
	}
	
}
