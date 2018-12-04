package jacusa.filter.factory.distance.lrtarrest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.basecall.GenericBaseCallCountFilter;
import jacusa.filter.cache.RecordProcessDataCache;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Apply2ReadsArrestPos2BaseCallCountSwitch;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.SpecificFilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.UniqueTraverse;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractLRTarrestDistanceFilterFactory 
extends AbstractFilterFactory {

	private final Apply2readsBaseCallCountSwitch bccSwitch;
	private final Fetcher<ArrestPosition2baseCallCount> filteredAp2bccFetcher;
	private final Fetcher<BaseCallCount> filteredBccExtractor;
	private final DataType<ArrestPos2BaseCallCountFilteredData> dataType;
	
	private int filterDistance;
	private double filterMinRatio;
	
	public AbstractLRTarrestDistanceFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option);
		this.bccSwitch = bccSwitch;
		filteredAp2bccFetcher = 
				new SpecificFilteredDataFetcher<>(getC(), filteredDataFetcher);
		filteredBccExtractor = 
				new Apply2ReadsArrestPos2BaseCallCountSwitch(
						bccSwitch.getApply2reads(),
						filteredAp2bccFetcher);
		dataType = filteredDataFetcher.getDataType();
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
	}

	@Override
	public void initDataTypeContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
		final ArrestPos2BaseCallCountFilteredData filteredData = builder.get(dataType);
		if (! filteredData.contains(getC())) {
			filteredData.add(getC(), new ArrestPosition2baseCallCount());
		}	
	}
	
	@Override
	public Options getOptions() {
		return new Options()
				.addOption(
						AbstractBaseCallCountFilterFactory.getDistanceOptionBuilder(filterDistance).build())
				.addOption(
						AbstractBaseCallCountFilterFactory.getMinRatioOptionBuilder(filterMinRatio).build());
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
			
				case "reads":
					final String optionValue = cmd.getOptionValue(longOpt);
					if (optionValue.isEmpty()) {
						throw new MissingOptionException("Missing value for " + longOpt);
					}
					final Set<RT_READS> tmpApply2reads = RTarrestMethod.processApply2Reads(optionValue);
					if (tmpApply2reads.size() == 0) {
						throw new IllegalArgumentException("Unknown value for " + longOpt);
					}
					bccSwitch.getApply2reads().clear();
					bccSwitch.getApply2reads().addAll(tmpApply2reads);
					parsed.add(option);
					break;
			}
		}
		return parsed;
	}

	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer filteredData) {
		// FIXME implement - maybe change of interface needed
		sb.append("TODO");
	}
	
	@Override
	protected AbstractFilter createFilter(CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		return new GenericBaseCallCountFilter(getC(),
			bccSwitch,
			filteredBccExtractor,	
			filterDistance, new FilterByRatio(filterMinRatio));
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(
			final AbstractConditionParameter conditionParameter,
			final SharedCache sharedCache) {

		final List<IncrementAdder> adder = new ArrayList<IncrementAdder>();
		final IncrementAdder baseCallAdder = new ArrayBaseCallAdder(filteredBccExtractor, sharedCache);
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
	
	public int getFilterDistance() {
		return filterDistance;
	}
	
	protected abstract List<ProcessRecord> createProcessRecord(final RegionDataCache regionDataCache);
	
	protected Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
}