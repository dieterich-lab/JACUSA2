package jacusa.method.rtarrest;


import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.pileupbuilder.OneConditionPileupDataBuilderOption;
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
import lib.data.AbstractData;
import lib.data.builder.factory.UnstrandedPileupBuilderFactory;
import lib.data.generator.DataGenerator;
import lib.data.has.hasPileupCount;
import lib.data.has.hasReadInfoCount;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory<T extends AbstractData & hasPileupCount & hasReadInfoCount> 
extends AbstractMethodFactory<T> {

	public final static String NAME = "rt-arrest";

	public RTArrestFactory(final DataGenerator<T> dataGenerator) {
		super(NAME, "Reverse Transcription Arrest - 2 conditions", 
				new RTArrestParameters<T>(2, new UnstrandedPileupBuilderFactory<T>()),
				dataGenerator);
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
			getParameter().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameter().setFormat(getResultFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<T>(
					getParameter(), getResultFormats()));
		}
	}

	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameter()));
		addACOption(new BaseConfigOption(getParameter()));
		
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
		addACOption(new MinMAPQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<T>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<T>(getParameter().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<T>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionPileupDataBuilderOption<T>(
					conditionIndex + 1, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, StatisticCalculator<T>> getStatistics() {
		Map<String, StatisticCalculator<T>> statistics = 
				new TreeMap<String, StatisticCalculator<T>>();
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<T, ?>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<T, ?>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<T, ?>>();

		List<AbstractFilterFactory<T, ?>> filterFactories = 
				new ArrayList<AbstractFilterFactory<T, ?>>(5);

		for (final AbstractFilterFactory<T, ?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<T>> getResultFormats() {
		Map<Character, AbstractOutputFormat<T>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<T>>();

		AbstractOutputFormat<T> resultFormat = null;

		resultFormat = new RTArrestResultFormat<T>(getParameter().getBaseConfig(), 
				getParameter().getFilterConfig(), getParameter().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);
		
		return resultFormats;
	}

	@Override
	public RTArrestParameters<T> getParameter() {
		return (RTArrestParameters<T>) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public AbstractWorker<T> createWorker() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void debug() {
		// set custom
		AbstractTool.getLogger().addDebug("Overwrite file format -> RTArrestDebugResultFormat");
		getParameter().setFormat(new RTArrestDebugResultFormat<T>(getParameter().getBaseConfig(), 
				getParameter().getFilterConfig(), getParameter().showReferenceBase()));
	}

	@Override
	public WorkerDispatcher<T> getWorkerDispatcher() {
		return new WorkerDispatcher<T>(this);
	}
	
}
