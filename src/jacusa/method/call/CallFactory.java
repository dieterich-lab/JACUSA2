package jacusa.method.call;

import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.CallParameters;
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
import jacusa.io.format.VCFcall;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import lib.cli.options.BaseConfigOption;
import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
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
import lib.data.basecall.PileupData;
import lib.data.builder.factory.UnstrandedPileupBuilderFactory;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class CallFactory extends AbstractMethodFactory<PileupData> {

	protected static WorkerDispatcher<PileupData> instance;
	
	public CallFactory(final int conditions) {
		super("call-" + (conditions == -1 ? "n" : conditions), 
				"Call variants - " + 
						(conditions == -1 ? "n" : conditions) + 
						(conditions == -1 || conditions == 2 ? " conditions" : " condition"), 
				new CallParameters<PileupData>(conditions, new UnstrandedPileupBuilderFactory<PileupData>())); // FIXME
	}

	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameter()));
		addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption<PileupData>(getParameter(), getFilterFactories()));
		
		addACOption(new StatisticFilterOption(getParameter().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));
		
		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
	}
		
	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<PileupData>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<PileupData>(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionBaseQualDataBuilderOption<PileupData>(getParameter().getConditionParameters(), getParameter()));
		
		// only add contions specific options when there are more than 1 conditions
		if (getParameter().getConditionsSize() > 1) {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
				addACOption(new MinMAPQConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinBASQConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MinCoverageConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new MaxDepthConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterFlagConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new FilterNHsamTagOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				addACOption(new FilterNMsamTagOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
				
				addACOption(new OneConditionBaseQualDataBuilderOption<PileupData>(
						conditionIndex + 1, 
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
			addACOption(new StatisticCalculatorOption<PileupData>(
					getParameter().getStatisticParameters(), getStatistics()));
		}
		
		// result format
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameter().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameter().setFormat(getResultFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<PileupData>(
					getParameter(), getResultFormats()));
		}
	}

	@Override
	public WorkerDispatcher<PileupData> getWorkerDispatcher() {
		if(instance == null) {
			instance = new WorkerDispatcher<PileupData>(this);
		}

		return instance;
	}
	
	public Map<String, StatisticCalculator<PileupData>> getStatistics() {
		final Map<String, StatisticCalculator<PileupData>> statistics = 
				new TreeMap<String, StatisticCalculator<PileupData>>();

		StatisticCalculator<PileupData> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<PileupData>(getParameter());
		statistics.put("DirMult", statistic);

		return statistics;
	}

	public Map<Character, AbstractFilterFactory<PileupData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<PileupData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<PileupData>>();

		final List<AbstractFilterFactory<PileupData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<PileupData>>(10);
		
		filterFactories.add(new CombinedDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new INDEL_DistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new HomozygousFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<PileupData>());
		filterFactories.add(new HomopolymerFilterFactory<PileupData>(getParameter()));

		for (final AbstractFilterFactory<PileupData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<PileupData>> getResultFormats() {
		final Map<Character, AbstractOutputFormat<PileupData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<PileupData>>();

		AbstractOutputFormat<PileupData> resultFormat = null;

		// BED like output
		resultFormat = new BED6call(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		resultFormat = new VCFcall(getParameter().getBaseConfig(), 
				getParameter().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public CallParameters<PileupData> getParameter() {
		return (CallParameters<PileupData>) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public AbstractWorker<PileupData> createWorker() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PileupData createData() {
		return new PileupData();
	}

	@Override
	public PileupData[] createReplicateData(final int n) {
		return new PileupData[n];
	}

	@Override
	public PileupData[][] createContainer(final int n) {
		return new PileupData[n][];
	}

	@Override
	public PileupData copyData(final PileupData dataContainer) {
		return new PileupData(dataContainer);
	}
	
	@Override
	public PileupData[] copyReplicateData(final PileupData[] dataContainer) {
		PileupData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public PileupData[][] copyContainer(final PileupData[][] dataContainer) {
		PileupData[][] ret = createContainer(dataContainer.length);
		
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new PileupData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}
	
}