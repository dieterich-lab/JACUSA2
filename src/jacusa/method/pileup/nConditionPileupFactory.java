package jacusa.method.pileup;

import jacusa.cli.options.BaseConfigOption;
import jacusa.cli.options.BedCoordinatesOption;
import jacusa.cli.options.FilterConfigOption;
import jacusa.cli.options.FilterModusOption;
import jacusa.cli.options.FormatOption;
import jacusa.cli.options.HelpOption;
import jacusa.cli.options.MaxThreadOption;
import jacusa.cli.options.ResultFileOption;
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.InvertStrandOption;
import jacusa.cli.options.condition.MaxDepthConditionOption;
import jacusa.cli.options.condition.MinBASQConditionOption;
import jacusa.cli.options.condition.MinCoverageConditionOption;
import jacusa.cli.options.condition.MinMAPQConditionOption;
import jacusa.cli.options.condition.filter.FilterFlagConditionOption;
import jacusa.cli.options.condition.filter.FilterNHsamTagOption;
import jacusa.cli.options.condition.filter.FilterNMsamTagOption;
import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.PileupParameters;
import jacusa.data.BaseQualData;
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
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class nConditionPileupFactory extends AbstractMethodFactory<BaseQualData> {

	private static MpileupWorkerDispatcher<BaseQualData> instance;
	
	public nConditionPileupFactory(int conditions) {
		super("pileup", "SAMtools like mpileup", 
				new PileupParameters<BaseQualData>(conditions, new UnstrandedPileupBuilderFactory<BaseQualData>()));
	}

	public void initParameters(final int conditions) {
		setParameters(new PileupParameters<BaseQualData>(conditions, new UnstrandedPileupBuilderFactory<BaseQualData>()));
	}

	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
		
		// result format
		if (getOuptutFormats().size() == 1 ) {
			Character[] a = getOuptutFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getOuptutFormats().get(a[0]));
		} else {
			getParameters().setFormat(getOuptutFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<BaseQualData>(
					getParameters(), getOuptutFormats()));
		}
	}
	
	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameters()));
		addACOption(new BaseConfigOption(getParameters()));
		addACOption(new FilterConfigOption<BaseQualData>(getParameters(), getFilterFactories()));
		
		addACOption(new ShowReferenceOption(getParameters()));
		addACOption(new HelpOption(CLI.getSingleton()));
		
		addACOption(new MaxThreadOption(getParameters()));
		addACOption(new WindowSizeOption(getParameters()));
		addACOption(new ThreadWindowSizeOption(getParameters()));
		
		addACOption(new BedCoordinatesOption(getParameters()));
		addACOption(new ResultFileOption(getParameters()));
	}
		
	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MinBASQConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new InvertStrandOption<BaseQualData>(getParameters().getConditionParameters()));
		
		addACOption(new OneConditionBaseQualDataBuilderOption<BaseQualData>(getParameters().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameters().getConditions(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new InvertStrandOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionBaseQualDataBuilderOption<BaseQualData>(conditionIndex + 1, getParameters().getConditionParameters().get(conditionIndex)));
		}
	}

	public Map<Character, AbstractOutputFormat<BaseQualData>> getOuptutFormats() {
		final Map<Character, AbstractOutputFormat<BaseQualData>> outputFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualData>>();

		AbstractOutputFormat<BaseQualData> outputFormat = 
				new PileupFormat(getParameters().getBaseConfig(), getParameters().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6call(getParameters());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	public Map<Character, AbstractFilterFactory<BaseQualData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<BaseQualData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<BaseQualData>>();

		final List<AbstractFilterFactory<BaseQualData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualData>>(10);
		
		filterFactories.add(new CombinedDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new INDEL_DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new HomozygousFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new MaxAlleleCountFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new HomopolymerFilterFactory<BaseQualData>(getParameters()));

		for (final AbstractFilterFactory<BaseQualData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}
	
	@Override
	public MpileupWorkerDispatcher<BaseQualData> getInstance(CoordinateProvider coordinateProvider) {
		if(instance == null) {
			instance = new MpileupWorkerDispatcher<BaseQualData>(coordinateProvider, getParameters());
		}

		return instance;
	}

	@Override
	public PileupParameters<BaseQualData> getParameters() {
		return (PileupParameters<BaseQualData>) super.getParameters();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}

	@Override
	public BaseQualData createData() {
		return new BaseQualData();
	}
	
	@Override
	public BaseQualData[] createReplicateData(final int n) {
		return new BaseQualData[n];
	}
	
	@Override
	public BaseQualData[][] createContainer(final int n) {
		return new BaseQualData[n][];
	}

	@Override
	public BaseQualData copyData(final BaseQualData dataContainer) {
		return new BaseQualData(dataContainer);
	}
	
	@Override
	public BaseQualData[] copyReplicateData(final BaseQualData[] dataContainer) {
		BaseQualData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public BaseQualData[][] copyContainer(final BaseQualData[][] dataContainer) {
		BaseQualData[][] ret = createContainer(dataContainer.length);

		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new BaseQualData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}
}
