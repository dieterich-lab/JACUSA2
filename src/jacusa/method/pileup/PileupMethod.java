package jacusa.method.pileup;

import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.PileupParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.io.format.pileup.BED6pileupResultFormat;
import jacusa.io.format.pileup.PileupLikeFormat;
import jacusa.worker.PileupWorker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.CollectReadSubstituionOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ResultFormatOption;
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
import lib.data.builder.factory.PileupDataAssemblerFactory;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.PileupCountBaseCallCountExtractor;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.ResultFormat;
import lib.method.AbstractMethod;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class PileupMethod 
extends AbstractMethod {
	
	private final Fetcher<BaseCallCount> bccFetcher;
	
	protected PileupMethod(
			final String name, 
			final PileupParameter parameter, 
			final PileupDataAssemblerFactory dataAssemblerFactory,
			final PileupBuilderFactory builderFactory) {

		super(name, parameter, dataAssemblerFactory);
		bccFetcher = new PileupCountBaseCallCountExtractor(DataType.PILEUP_COUNT.getFetcher());
	}
	
	protected void initGlobalACOptions() {
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption(getParameter(), getResultFormats()));
		}

		addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption(getParameter(), getFilterFactories()));
		
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

	public Map<Character, ResultFormat> getResultFormats() {
		final Map<Character, ResultFormat> outputFormats = 
				new HashMap<Character, ResultFormat>();

		ResultFormat outputFormat = new PileupLikeFormat(getName(), getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);

		outputFormat = new BED6pileupResultFormat(getName(), getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	public Map<Character, AbstractFilterFactory> getFilterFactories() {
		final Map<Character, AbstractFilterFactory> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory>();

		final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredBccData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BCC);
		final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanData = 
				new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		final List<AbstractFilterFactory> filterFactories = Arrays.asList(
				new ExcludeSiteFilterFactory(),
				new CombinedFilterFactory(
						bccFetcher,
						filteredBccData),
				new INDEL_FilterFactory(
						bccFetcher, 
						filteredBccData),
				new ReadPositionDistanceFilterFactory(
						bccFetcher, 
						filteredBccData),
				new SpliceSiteFilterFactory(
						bccFetcher, 
						filteredBccData),
				new HomozygousFilterFactory(getParameter().getConditionsSize(), bccFetcher),
				new MaxAlleleCountFilterFactory(bccFetcher),
				new HomopolymerFilterFactory(filteredBooleanData) );

		for (final AbstractFilterFactory filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	@Override
	public PileupParameter getParameter() {
		return (PileupParameter) super.getParameter();
	}
	
	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
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
		return new PileupWorker(this, threadId);
	}
	
	/*
	 * Factory
	 */
	
	public static class PileupBuilderFactory extends AbstractBuilderFactory {

		private final PileupParameter parameter;
		
		public PileupBuilderFactory(final PileupParameter parameter) {
			super(parameter);
			this.parameter = parameter;
		}
		
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
			if (parameter.getReadSubstitutions() != null) {
				addBaseSubstitution(builder, DataType.BASE_SUBST);
			}
		}
		
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}
		
	}
	
	public static class Factory extends AbstractMethod.AbstractFactory {
	
		public static final String NAME = "pileup";
		public static final String DESC = "samtools like mpileup";
		
		public Factory() {
			this(1);
		}
		
		public Factory(final int conditions) {
			super(NAME, DESC, conditions);
		}
		
		@Override
		public PileupMethod createMethod() {
			final PileupParameter parameter = new PileupParameter(getConditions());
			final PileupBuilderFactory builderFactory = new PileupBuilderFactory(parameter);
			
			final PileupDataAssemblerFactory dataAssemblerFactory = 
					new PileupDataAssemblerFactory(builderFactory);
			
			return new PileupMethod(
					getName(),
					parameter,
					dataAssemblerFactory, 
					builderFactory);
		}
		
		protected PileupParameter createParameter(final int conditions) {
			return new PileupParameter(conditions);
		}
		
		@Override
		public Factory createFactory(int conditions) {
			return new Factory(conditions);
		}
		
	}
	
}
