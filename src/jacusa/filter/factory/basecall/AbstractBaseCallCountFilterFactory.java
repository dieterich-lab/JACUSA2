package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.JACUSA;
import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.basecall.GenericBaseCallCountFilter;
import jacusa.filter.cache.RecordProcessDataCache;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
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
		filteredBccFetcher = new SpecificFilteredDataFetcher<>(getC(), filteredDataFetcher);
		dataType = filteredDataFetcher.getDataType();
		
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
	public Set<Option> processCLI(final CommandLine cmd) throws MissingOptionException {
		final Set<Option> parsed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "distance":
				filterDistance = Integer.parseInt(cmd.getOptionValue(longOpt));
				parsed.add(option);
				break;
				
			case "minRatio":
				filterMinRatio = Double.parseDouble(cmd.getOptionValue(longOpt));
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}

	@Override
	public void initDataTypeContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
		final BaseCallCountFilteredData filteredData = builder.get(dataType);
		if (! filteredData.contains(getC())) {
			filteredData.add(getC(), JACUSA.bccFactory.create());
		}
	}
	
	@Override
	public AbstractFilter createFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {
		return new GenericBaseCallCountFilter(getC(),
				observedBccFetcher,
				filteredBccFetcher,	
				filterDistance, new FilterByRatio(getFilterMinRatio()));
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(final AbstractConditionParameter conditionParameter,
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
		
		final ValidatedRegionDataCache regionDataCache = new ValidatedRegionDataCache(adder, validator, sharedCache);
		final UniqueTraverse uniqueBaseCallCache = new UniqueTraverse(regionDataCache);
		return new RecordProcessDataCache(uniqueBaseCallCache, createProcessRecord(uniqueBaseCallCache));
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
