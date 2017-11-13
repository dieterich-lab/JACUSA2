package jacusa.method.pileup;

import jacusa.cli.options.pileupbuilder.OneConditionPileupDataBuilderOption;
import jacusa.cli.parameters.PileupParameters;
import jacusa.data.validator.DummyValidator;
import jacusa.data.validator.ParallelDataValidator;
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
import jacusa.io.format.PileupFormat;
import jacusa.worker.PileupWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import lib.data.basecall.BaseCallData;
import lib.data.builder.factory.UnstrandedPileupBuilderFactory;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.hasPileupCount;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class PileupFactory<T extends AbstractData & hasPileupCount> 
extends AbstractMethodFactory<T> {
	
	public PileupFactory(final int conditions, final DataGenerator<T> dataGenerator) {
		super("pileup", "SAMtools like mpileup", 
				new PileupParameters<T>(conditions, new UnstrandedPileupBuilderFactory<T>()),
				dataGenerator);
	}

	public void initGeneralParameter(int conditionSize) {
		if (conditionSize == 0) {
			conditionSize = 3;
		}
		setParameters(new PileupParameters<T>(conditionSize, new UnstrandedPileupBuilderFactory<T>()));
	}

	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
		
		// result format
		if (getOuptutFormats().size() == 1 ) {
			Character[] a = getOuptutFormats().keySet().toArray(new Character[1]);
			getParameter().setFormat(getOuptutFormats().get(a[0]));
		} else {
			getParameter().setFormat(getOuptutFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<T>(
					getParameter(), getOuptutFormats()));
		}
	}
	
	protected void initGlobalACOptions() {
		// addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		// addACOption(new FilterConfigOption<T>(getParameter(), getFilterFactories()));
		
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
		
		addACOption(new OneConditionPileupDataBuilderOption<T>(getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionPileupDataBuilderOption<T>(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}

	public Map<Character, AbstractOutputFormat<T>> getOuptutFormats() {
		final Map<Character, AbstractOutputFormat<T>> outputFormats = 
				new HashMap<Character, AbstractOutputFormat<T>>();

		AbstractOutputFormat<T> outputFormat = 
				new PileupFormat<T>(getParameter().getBaseConfig(), getParameter().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6call<T>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
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
	
	@Override
	public WorkerDispatcher<T> getWorkerDispatcher() {
		return new WorkerDispatcher<T>(this);
	}

	@Override
	public PileupParameters<T> getParameter() {
		return (PileupParameters<T>) super.getParameter();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}

	@Override
	public PileupWorker<T> createWorker() {
		final ParallelDataValidator<T> parallelDataValidator = new DummyValidator<T>();
		return new PileupWorker<T>(getWorkerDispatcher(), parallelDataValidator, getParameter());
	}

}
