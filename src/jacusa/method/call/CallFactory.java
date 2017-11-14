package jacusa.method.call;

import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.pileupbuilder.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import jacusa.data.validator.MinCoverageValidator;
import jacusa.data.validator.ParallelDataValidator;
import jacusa.data.validator.VariantSiteValidator;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.CombinedDistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6call;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.worker.CallWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.FormatOption;
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
import lib.data.AbstractData;
import lib.data.BaseCallData;
import lib.data.builder.factory.PileupDataBuilderFactory;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.hasPileupCount;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class CallFactory<T extends AbstractData & hasPileupCount> 
extends AbstractMethodFactory<T> {

	public CallFactory(final CallParameter<T> callParameter, final DataGenerator<T> dataGenerator) {
		super("call-" + (callParameter.getConditionsSize() == -1 ? "n" : callParameter.getConditionsSize()), 
				"Call variants - " + 
						(callParameter.getConditionsSize() == -1 ? "n" : callParameter.getConditionsSize()) + 
						(callParameter.getConditionsSize() == -1 || callParameter.getConditionsSize() == 2 ? " conditions" : " condition"), 
				callParameter,
				new PileupDataBuilderFactory<T>(callParameter),
				dataGenerator);
	}

	protected void initGlobalACOptions() {
		
		// addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		// addACOption(new FilterConfigOption<T>(getParameter(), getFilterFactories()));
		
		addACOption(new StatisticFilterOption(getParameter().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));
		
		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
	}
	
	@Override
	public boolean checkState() {
		return true;
	}
	
	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<T>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<T>(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionLibraryTypeOption<T>(getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
				addACOption(new MinMAPQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinBASQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinCoverageConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MaxDepthConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterFlagConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new FilterNHsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterNMsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new OneConditionLibraryTypeOption<T>(
						conditionIndex, 
						getParameter().getConditionParameters().get(conditionIndex),
						getParameter()));
			}
		}
	}

	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
		
		// statistic
		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			getParameter().getStatisticParameters().setStatisticCalculator(
					getStatistics().get(a[0]));
		} else {
			addACOption(new StatisticCalculatorOption<T>(
					getParameter().getStatisticParameters(), getStatistics()));
		}
		
		// result format
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameter().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameter().setFormat(getResultFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<T>(
					getParameter(), getResultFormats()));
		}
	}
	
	public Map<String, StatisticCalculator<T>> getStatistics() {
		final Map<String, StatisticCalculator<T>> statistics = 
				new TreeMap<String, StatisticCalculator<T>>();

		StatisticCalculator<T> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<T>(getParameter());
		statistics.put("DirMult", statistic);

		return statistics;
	}

	public Map<Character, AbstractFilterFactory<T, ?>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<T, ?>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<T, ?>>();

		final List<AbstractFilterFactory<T, ?>> filterFactories = 
				new ArrayList<AbstractFilterFactory<T, ?>>(10);
		
		final DataGenerator<BaseCallData> dataGenerator = new BaseCallDataGenerator();
		filterFactories.add(new CombinedDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new INDEL_DistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new ReadPositionDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new HomozygousFilterFactory<T>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<T>());
		filterFactories.add(new HomopolymerFilterFactory<T, BaseCallData>(dataGenerator));

		for (final AbstractFilterFactory<T,?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<T>> getResultFormats() {
		final Map<Character, AbstractOutputFormat<T>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<T>>();

		AbstractOutputFormat<T> resultFormat = null;

		// BED like output
		resultFormat = new BED6call<T>(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		/*
		resultFormat = new VCFcall<T>(getParameter().getBaseConfig(), 
				getParameter().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);
		*/

		return resultFormats;
	}

	@Override
	public CallParameter<T> getParameter() {
		return (CallParameter<T>) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}
	
	@Override
	public List<ParallelDataValidator<T>> getParallelDataValidators() {
		final List<ParallelDataValidator<T>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<T>(getParameter().getConditionParameters()));
		validators.add(new VariantSiteValidator<T>());
		return validators;
	}

	@Override
	public CallWorker<T> createWorker(final int threadId) {
		return new CallWorker<T>(getWorkerDispatcher(), threadId, 
				getParallelDataValidators(), 
				(CallParameter<T>)getParameter());
	}
	
}
