package jacusa.method.rtarrest;

import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6call;
import jacusa.io.format.RTArrestDebugResultFormat;
import jacusa.io.format.RTArrestResultFormat;
import jacusa.method.call.statistic.StatisticCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import lib.cli.options.BaseConfigOption;
import lib.cli.options.BedCoordinatesOption;
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
import lib.data.BaseQualReadInfoData;
import lib.data.builder.UnstrandedPileupBuilderFactory;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory extends AbstractMethodFactory<BaseQualReadInfoData> {

	public final static String NAME = "rt-arrest";

	private static WorkerDispatcher<BaseQualReadInfoData> instance;

	public RTArrestFactory() {
		super(NAME, "Reverse Transcription Arrest - 2 conditions", 
				new RTArrestParameters<BaseQualReadInfoData>(
						2, new UnstrandedPileupBuilderFactory<BaseQualReadInfoData>()));
	}

	/*
	public void initParameters(final int conditions) {
		setParameters(new RTArrestParameters<BaseQualReadInfoData>(conditions));
	}
	*/

	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
		
		// result format
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameters().setFormat(getResultFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<BaseQualReadInfoData>(
					getParameters(), getResultFormats()));
		}
	}

	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameters()));
		addACOption(new BaseConfigOption(getParameters()));
		
		addACOption(new StatisticFilterOption(getParameters().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameters()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addACOption(new MaxThreadOption(getParameters()));
		addACOption(new WindowSizeOption(getParameters()));
		addACOption(new ThreadWindowSizeOption(getParameters()));
		
		addACOption(new BedCoordinatesOption(getParameters()));
		addACOption(new ResultFileOption(getParameters()));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		addACOption(new MinBASQConditionOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<BaseQualReadInfoData>(getParameters().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameters().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionBaseQualDataBuilderOption<BaseQualReadInfoData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
		}
	}
	
	public Map<String, StatisticCalculator<BaseQualReadInfoData>> getStatistics() {
		Map<String, StatisticCalculator<BaseQualReadInfoData>> statistics = 
				new TreeMap<String, StatisticCalculator<BaseQualReadInfoData>>();
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<BaseQualReadInfoData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<BaseQualReadInfoData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<BaseQualReadInfoData>>();

		List<AbstractFilterFactory<BaseQualReadInfoData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualReadInfoData>>(5);

		for (final AbstractFilterFactory<BaseQualReadInfoData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<BaseQualReadInfoData>> getResultFormats() {
		Map<Character, AbstractOutputFormat<BaseQualReadInfoData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualReadInfoData>>();

		AbstractOutputFormat<BaseQualReadInfoData> resultFormat = null;

		resultFormat = new RTArrestResultFormat(getParameters().getBaseConfig(), 
				getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);
		
		return resultFormats;
	}

	@Override
	public RTArrestParameters<BaseQualReadInfoData> getParameters() {
		return (RTArrestParameters<BaseQualReadInfoData>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public WorkerDispatcher<BaseQualReadInfoData> getWorkerDispatcher() {
		if(instance == null) {
			instance = new WorkerDispatcher<BaseQualReadInfoData>(this);
		}
		return instance;
	}

	@Override
	public AbstractWorker<BaseQualReadInfoData> createWorker() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BaseQualReadInfoData createData() {
		return new BaseQualReadInfoData();
	}
	
	@Override
	public BaseQualReadInfoData[] createReplicateData(final int n) {
		return new BaseQualReadInfoData[n];
	}
	
	@Override
	public BaseQualReadInfoData[][] createContainer(final int n) {
		return new BaseQualReadInfoData[n][];
	}

	@Override
	public BaseQualReadInfoData copyData(final BaseQualReadInfoData dataContainer) {
		return new BaseQualReadInfoData(dataContainer);
	}
	
	@Override
	public BaseQualReadInfoData[] copyReplicateData(final BaseQualReadInfoData[] dataContainer) {
		BaseQualReadInfoData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualReadInfoData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public BaseQualReadInfoData[][] copyContainer(final BaseQualReadInfoData[][] dataContainer) {
		BaseQualReadInfoData[][] ret = createContainer(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualReadInfoData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new BaseQualReadInfoData(dataContainer[i][j]);
			}	
		}

		return ret;
	}

	@Override
	public void debug() {
		// set custom
		AbstractTool.getLogger().addDebug("Overwrite file format -> RTArrestDebugResultFormat");
		getParameters().setFormat(new RTArrestDebugResultFormat(getParameters().getBaseConfig(), 
				getParameters().getFilterConfig(), getParameters().showReferenceBase()));
	}
	
}
