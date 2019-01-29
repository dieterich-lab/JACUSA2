package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.JACUSA;
import jacusa.filter.Filter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.GenericBaseCallCountFilter;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.homopolymer.RecordProcessDataCache;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.SpecificFilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.UniqueTraverse;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * Tested in @see test.jacusa.filter.factory.basecall.BaseCallCountFilterFactoryTest
 */
public abstract class AbstractBaseCallCountFilterFactory
extends AbstractFilterFactory {

	private Fetcher<BaseCallCount> observedBccFetcher;
	private final Fetcher<BaseCallCount> filteredBccFetcher;
	private final DataType<BaseCallCountFilteredData> dataType;
	
	private int filterDistance;
	private double filterMinRatio;

	private final BaseCallCount.AbstractParser baseCallCountParser;
	
	public AbstractBaseCallCountFilterFactory(
			final Option option,
			final Fetcher<BaseCallCount> observedBccFetcher, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option);
		
		this.observedBccFetcher = observedBccFetcher;
		filteredBccFetcher 		= new SpecificFilteredDataFetcher<>(getC(), filteredDataFetcher);
		dataType 				= filteredDataFetcher.getDataType();
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;

		baseCallCountParser = new DefaultBaseCallCount.Parser(Util.EMPTY_FIELD, Util.VALUE_SEP);
	}
	
	@Override
	public Options getOptions() {
		return new Options()
				.addOption(getDistanceOptionBuilder(filterDistance).build())
				.addOption(getMinRatioOptionBuilder(filterMinRatio).build());
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) {
		final Set<Option> parsed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "distance":
				filterDistance = parseDistance(cmd, longOpt);
				parsed.add(option);
				break;
				
			case "minRatio":
				filterMinRatio = parseMinRatio(cmd, longOpt);
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}

	public static int parseDistance(final CommandLine cmd, final String longOpt) {
		final int tmpDistance = Integer.parseInt(cmd.getOptionValue(longOpt));
		if (tmpDistance <= 0) {
			throw new IllegalArgumentException(longOpt + " needs to be > 0");
		}
		return tmpDistance;
	}

	public static double parseMinRatio(final CommandLine cmd, final String longOpt) {
		final double tmpMinRatio = Double.parseDouble(cmd.getOptionValue(longOpt)); 
		if (tmpMinRatio < 0.0 || tmpMinRatio > 1.0) {
			throw new IllegalArgumentException(longOpt + " needs to be within [0.0, 1.0]");
		}
		return tmpMinRatio;
	}
	
	@Override
	public void initDataTypeContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
		final BaseCallCountFilteredData filteredData = builder.get(dataType);
		if (! filteredData.contains(getC())) {
			filteredData.add(getC(), JACUSA.BCC_FACTORY.create());
		}
	}
	
	@Override
	public Filter createFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {
		
		return new GenericBaseCallCountFilter(getC(),
				observedBccFetcher,
				filteredBccFetcher,	
				filterDistance, new FilterByRatio(getFilterMinRatio()));
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(
			final ConditionParameter conditionParameter,
			final SharedCache sharedCache) {

		final List<IncrementAdder> adder = new ArrayList<IncrementAdder>();
		final IncrementAdder baseCallAdder = new ArrayBaseCallAdder(filteredBccFetcher, sharedCache);
		adder.add(baseCallAdder);
		
		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), baseCallAdder));
		}
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}
		
		final ValidatedRegionDataCache regionDataCache = 
				new ValidatedRegionDataCache(adder, validator, sharedCache);
		final UniqueTraverse uniqueBaseCallCache = 
				new UniqueTraverse(regionDataCache);
		return new RecordProcessDataCache(
				uniqueBaseCallCache, 
				createProcessRecord(uniqueBaseCallCache));
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer filteredData) {
		baseCallCountParser.wrap(filteredBccFetcher.fetch(filteredData));
	}
	
	public int getFilterDistance() {
		return filterDistance;
	}

	public double getFilterMinRatio() {
		return filterMinRatio;
	}

	/*
	public int getMinCount() {
		return filterMinCount;
	}
	*/
	
	@Override
	public String toString() {
		return Character.toString(getC());
	}
	
	protected abstract List<ProcessRecord> createProcessRecord(final RegionDataCache regionDataCache);
	
	public static Option.Builder getDistanceOptionBuilder(final int filterDistance) {
		return Option.builder()
				.argName("DISTANCE")
				.longOpt("distance")
				.hasArg(true)
				.desc("Filter base calls within distance to feature. Default: " + filterDistance);
	}
	
	public static Option.Builder getMinRatioOptionBuilder(final double minRatio) {
		return Option.builder()
				.argName("MINRATIO")
				.longOpt("minRatio")
				.hasArg(true)
				.desc("Minimal ratio of base calls to pass filtering. Default: " + minRatio);
	}
	
	
}
