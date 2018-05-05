package jacusa.method.call;

import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.distance.CombinedFilterFactory;
import jacusa.filter.factory.distance.INDEL_FilterFactory;
import jacusa.filter.factory.distance.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.distance.SpliceSiteFilterFactory;
import jacusa.io.format.call.BED6callResultFormat;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.worker.CallWorker;

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
import lib.data.CallData;
import lib.data.builder.factory.PileupDataBuilderFactory;
import lib.data.generator.CallDataGenerator;
import lib.data.result.StatisticResult;
import lib.data.validator.MinCoverageValidator;
import lib.data.validator.ParallelDataValidator;
import lib.data.validator.VariantSiteValidator;
import lib.io.AbstractResultFormat;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class CallFactory 
extends AbstractMethodFactory<CallData, StatisticResult<CallData>> {

	public CallFactory(final CallParameter callParameter) {
		super("call-" + (callParameter.getConditionsSize() == -1 ? "n" : callParameter.getConditionsSize()), 
				"Call variants - " + 
						(callParameter.getConditionsSize() == -1 ? "n" : callParameter.getConditionsSize()) + 
						(callParameter.getConditionsSize() == -1 || callParameter.getConditionsSize() == 2 ? " conditions" : " condition"), 
				callParameter,
				new PileupDataBuilderFactory<CallData>(callParameter),
				new CallDataGenerator());
	}

	protected void initGlobalACOptions() {
		// statistic option only if there is a choice
		if (getStatistics().size() > 1 ) {
			addACOption(new StatisticCalculatorOption<CallData>(
					getParameter().getStatisticParameters(), getStatistics()));
		}

		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption<CallData, StatisticResult<CallData>>(
					getParameter(), getResultFormats()));
		}
		
		addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption<CallData>(getParameter(), getFilterFactories()));
		
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
		addACOption(new MinMAPQConditionOption<CallData>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<CallData>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<CallData>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<CallData>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<CallData>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<CallData>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<CallData>(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionLibraryTypeOption<CallData>(getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
				addACOption(new MinMAPQConditionOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinBASQConditionOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinCoverageConditionOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MaxDepthConditionOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterFlagConditionOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new FilterNHsamTagOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterNMsamTagOption<CallData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new OneConditionLibraryTypeOption<CallData>(
						conditionIndex, 
						getParameter().getConditionParameters().get(conditionIndex),
						getParameter()));
			}
		}
	}
	
	public Map<String, AbstractStatisticCalculator<CallData>> getStatistics() {
		final Map<String, AbstractStatisticCalculator<CallData>> statistics = 
				new TreeMap<String, AbstractStatisticCalculator<CallData>>();

		AbstractStatisticCalculator<CallData> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<CallData>(getParameter());
		statistics.put("DirMult", statistic);

		return statistics;
	}

	public Map<Character, AbstractFilterFactory<CallData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<CallData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<CallData>>();

		final List<AbstractFilterFactory<CallData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<CallData>>(10);
		
		filterFactories.add(new CombinedFilterFactory<CallData>());
		filterFactories.add(new INDEL_FilterFactory<CallData>());
		filterFactories.add(new ReadPositionDistanceFilterFactory<CallData>());
		filterFactories.add(new SpliceSiteFilterFactory<CallData>());
		filterFactories.add(new HomozygousFilterFactory<CallData>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<CallData>());

		// FIXME filterFactories.add(new HomopolymerFilterFactory<CallData>());

		for (final AbstractFilterFactory<CallData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractResultFormat<CallData, StatisticResult<CallData>>> getResultFormats() {
		final Map<Character, AbstractResultFormat<CallData, StatisticResult<CallData>>> resultFormats = 
				new HashMap<Character, AbstractResultFormat<CallData, StatisticResult<CallData>>>();

		AbstractResultFormat<CallData, StatisticResult<CallData>> resultFormat = null;

		// BED like output
		resultFormat = new BED6callResultFormat<CallData, StatisticResult<CallData>>(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// FIXME VCF output 
		/*
		resultFormat = new VCFcall<T>(getParameter().getBaseConfig(), 
				getParameter().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);
		*/

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
	public List<ParallelDataValidator<CallData>> getParallelDataValidators() {
		final List<ParallelDataValidator<CallData>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<CallData>(getParameter().getConditionParameters()));
		validators.add(new VariantSiteValidator<CallData>());
		return validators;
	}

	@Override
	public CallWorker createWorker(final int threadId) {
		return new CallWorker(getWorkerDispatcher(), threadId,
				getParameter().getResultFormat().createCopyTmp(threadId),
				getParallelDataValidators(), 
				getParameter());
	}
	
}
