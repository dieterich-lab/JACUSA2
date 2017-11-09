package jacusa.method.pileup;

import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.PileupParameters;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class nConditionPileupFactory extends AbstractMethodFactory<PileupData> {

	private static WorkerDispatcher<PileupData> instance;
	
	public nConditionPileupFactory(int conditions) {
		super("pileup", "SAMtools like mpileup", 
				new PileupParameters<PileupData>(conditions, new UnstrandedPileupBuilderFactory<PileupData>()));
	}

	public void initGeneralParameter(final int conditionSize) {
		setParameters(new PileupParameters<PileupData>(conditionSize, new UnstrandedPileupBuilderFactory<PileupData>()));
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
			addACOption(new FormatOption<PileupData>(
					getParameter(), getOuptutFormats()));
		}
	}
	
	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameter()));
		addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption<PileupData>(getParameter(), getFilterFactories()));
		
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
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<PileupData>(conditionIndex + 1, getParameter().getConditionParameters().get(conditionIndex)));
			// TODO addACOption(new InvertStrandOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionBaseQualDataBuilderOption<PileupData>(
					conditionIndex + 1, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}

	public Map<Character, AbstractOutputFormat<PileupData>> getOuptutFormats() {
		final Map<Character, AbstractOutputFormat<PileupData>> outputFormats = 
				new HashMap<Character, AbstractOutputFormat<PileupData>>();

		AbstractOutputFormat<PileupData> outputFormat = 
				new PileupFormat(getParameter().getBaseConfig(), getParameter().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6call(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
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
	
	@Override
	public WorkerDispatcher<PileupData> getWorkerDispatcher() {
		if (instance == null) {
			instance = new WorkerDispatcher<PileupData>(this);
		}

		return instance;
	}

	@Override
	public PileupParameters<PileupData> getParameter() {
		return (PileupParameters<PileupData>) super.getParameter();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
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

	@Override
	public AbstractWorker<PileupData> createWorker() {
		// TODO Auto-generated method stub
		return null;
	}
}
