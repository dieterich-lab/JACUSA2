package jacusa.method.rtarrest;

import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.LRTarrestHomozygousFilterFactory;
import jacusa.filter.factory.LRTarrestMaxAlleleCountFilterFactory;
import jacusa.filter.factory.distance.LRTarrestCombinedFilterFactory;
import jacusa.filter.factory.distance.LRTarrestINDEL_FilterFactory;
import jacusa.filter.factory.distance.LRTarrestReadPositionFilterFactory;
import jacusa.filter.factory.distance.LRTarrestSpliceSiteFilterFactory;
import jacusa.io.writer.BED6lrtArrestResultFormat2;
import jacusa.io.writer.BED6rtArrestResultFormat1;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import jacusa.worker.LRTarrestWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
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
import lib.data.LRTarrestData;
import lib.data.builder.factory.LRTarrestDataBuilderFactory;
import lib.data.generator.LRTarrestDataGenerator;
import lib.data.result.StatisticResult;
import lib.data.validator.LinkedRTArrestVariantParallelPileup;
import lib.data.validator.MinCoverageValidator;
import lib.data.validator.ParallelDataValidator;
import lib.io.AbstractResultFormat;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class LRTarrestFactory 
extends AbstractMethodFactory<LRTarrestData, StatisticResult<LRTarrestData>> {
	
	public final static String NAME = "lrt-arrest";

	public LRTarrestFactory(final LRTarrestParameter rtArrestParameter) {
		super(NAME, "Linkage arrest to base substitution - 2 conditions", 
				rtArrestParameter, 
				new LRTarrestDataBuilderFactory<LRTarrestData>(rtArrestParameter), 
				new LRTarrestDataGenerator());
	}

	protected void initGlobalACOptions() {
		// statistic option only if there is a choice
		// statistic option only if there is a choice
		if (getStatistics().size() > 1 ) {
			addACOption(new StatisticCalculatorOption<LRTarrestData>(
					getParameter().getStatisticParameters(), getStatistics()));
		}
		
		// result format option only if there is a choice		
		if (getResultFormats().size() == 1 ) {
			addACOption(new ResultFormatOption<LRTarrestData, StatisticResult<LRTarrestData>>(
					getParameter(), getResultFormats()));
		}
		
		addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption<LRTarrestData>(getParameter(), getFilterFactories()));
		
		addACOption(new StatisticFilterOption(getParameter().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
		
		addACOption(new DebugModusOption(getParameter()));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<LRTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<LRTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<LRTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<LRTarrestData>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<LRTarrestData>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<LRTarrestData>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<LRTarrestData>(getParameter().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<LRTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionLibraryTypeOption<LRTarrestData>(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatisticCalculator<LRTarrestData>> getStatistics() {
		final Map<String, AbstractStatisticCalculator<LRTarrestData>> statistics = 
				new TreeMap<String, AbstractStatisticCalculator<LRTarrestData>>();

		final List<AbstractStatisticCalculator<LRTarrestData>> tmpList = new ArrayList<AbstractStatisticCalculator<LRTarrestData>>(5);
		tmpList.add(new DummyStatistic<LRTarrestData>());
		tmpList.add(new BetaBinomial<LRTarrestData>());
		
		for (final AbstractStatisticCalculator<LRTarrestData> statistic : tmpList) {
			statistics.put(statistic.getName(), statistic);
		}
		
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<LRTarrestData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<LRTarrestData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<LRTarrestData>>();

		List<AbstractFilterFactory<LRTarrestData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<LRTarrestData>>(5);

		filterFactories.add(new LRTarrestMaxAlleleCountFilterFactory<LRTarrestData>());
		filterFactories.add(new LRTarrestHomozygousFilterFactory<LRTarrestData>(getParameter()));
		filterFactories.add(new LRTarrestCombinedFilterFactory<LRTarrestData>());
		filterFactories.add(new LRTarrestReadPositionFilterFactory<LRTarrestData>());
		filterFactories.add(new LRTarrestINDEL_FilterFactory<LRTarrestData>());
		filterFactories.add(new LRTarrestSpliceSiteFilterFactory<LRTarrestData>());
		
		for (final AbstractFilterFactory<LRTarrestData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractResultFormat<LRTarrestData, StatisticResult<LRTarrestData>>> getResultFormats() {
		Map<Character, AbstractResultFormat<LRTarrestData, StatisticResult<LRTarrestData>>> resultFormats = 
				new HashMap<Character, AbstractResultFormat<LRTarrestData, StatisticResult<LRTarrestData>>>();

		AbstractResultFormat<LRTarrestData, StatisticResult<LRTarrestData>> resultFormat = null;

		resultFormat = new BED6rtArrestResultFormat1<LRTarrestData, StatisticResult<LRTarrestData>>(getParameter());
		resultFormat = new BED6lrtArrestResultFormat2<LRTarrestData, StatisticResult<LRTarrestData>>(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);
		
		return resultFormats;
	}

	@Override
	public LRTarrestParameter getParameter() {
		return (LRTarrestParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator<LRTarrestData>> getParallelDataValidators() {
		final List<ParallelDataValidator<LRTarrestData>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<LRTarrestData>(getParameter().getConditionParameters()));
		validators.add(new LinkedRTArrestVariantParallelPileup<LRTarrestData>());
		return validators;
	}
	
	@Override
	public LRTarrestWorker createWorker(final int threadId) {
		return new LRTarrestWorker(getWorkerDispatcher(), threadId,
				getParameter().getResultFormat().createCopyTmp(threadId),
				getParallelDataValidators(), getParameter());
	}
	
	
	@Override
	public void debug() {
		// set custom
		AbstractTool.getLogger().addDebug("Add additional column(s) in output start,inner,end!");
	}
	
}
