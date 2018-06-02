package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.BaseCallFilter;
import jacusa.filter.cache.RecordProcessDataCache;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.basecall.BaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.ConditionContainer;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.BaseCallCountFilterDataExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.UniqueTraverse;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MaxDepthBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.io.ResultWriterUtils;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractBaseCallCountFilterFactory<T extends AbstractData & HasBaseCallCountFilterData & HasReferenceBase>
extends AbstractDataFilterFactory<T> {

	private BaseCallCountExtractor<T> observed;
	private BaseCallCountExtractor<T> filtered;
	
	private int filterDistance;
	private double filterMinRatio;
		
	public AbstractBaseCallCountFilterFactory(final char c, final String desc,
			final BaseCallCountExtractor<T> observed, final BaseCallCountExtractor<T> filtered, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(c, desc + "\n   Default: " + 
			defaultFilterDistance + ":" 
			+ defaultFilterMinRatio + 
			" (" + c+ ":distance:min_ratio)");

		this.observed = observed;
		this.filtered = filtered;
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
	}
	
	public AbstractBaseCallCountFilterFactory(final char c, final String desc,
			final BaseCallCountExtractor<T> observed, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {
		this(c, desc, observed, new BaseCallCountFilterDataExtractor<T>(c), defaultFilterDistance, defaultFilterMinRatio);
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();

		options.addOption(Option.builder("distance")
				.hasArg(true)
				.desc("Default: " + getDistance())
				.build());

		options.addOption(Option.builder("minRatio")
				.hasArg(true)
				.desc("Default: " + getMinRatio())
				.build());
		
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		// format D:distance:minRatio
		// :minCount

		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "distance":
				filterDistance = Integer.parseInt(cmd.getOptionValue(opt));
				break;
				
			case "minRatio":
				filterMinRatio = Double.parseDouble(cmd.getOptionValue(opt));
				break;

			default:
				break;
			}
		}
	}

	@Override
	public AbstractFilter<T> createFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		return new BaseCallFilter<T>(getC(),
				getObserved(),
				getFiltered(),	
				getDistance(), new FilterRatio(getMinRatio()));
	}

	@Override
	public RecordWrapperDataCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final CoordinateController coordinateController) {

		final List<IncrementAdder<T>> adder = new ArrayList<IncrementAdder<T>>();
		final BaseCallAdder<T> baseCallAdder = new ArrayBaseCallAdder<T>(getFiltered(), coordinateController);
		adder.add(baseCallAdder);
		
		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		if (conditionParameter.getMaxDepth() > 0) {
			validator.add(new MaxDepthBaseCallValidator(conditionParameter.getMaxDepth(), baseCallAdder));
		}
		validator.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validator.add(new MinBASQBaseCallValidator(conditionParameter.getMinBASQ()));
		}
		
		final ValidatedRegionDataCache<T> regionDataCache = new ValidatedRegionDataCache<T>(adder, validator, coordinateController);
		final UniqueTraverse<T> uniqueBaseCallCache = new UniqueTraverse<T>(regionDataCache);
		return new RecordProcessDataCache<T>(uniqueBaseCallCache, createProcessRecord(uniqueBaseCallCache));
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		ResultWriterUtils.addBaseCallCount(sb, getFiltered().getBaseCallCount(data));
	}
	
	public int getDistance() {
		return filterDistance;
	}

	public double getMinRatio() {
		return filterMinRatio;
	}

	/*
	public int getMinCount() {
		return filterMinCount;
	}
	*/

	protected BaseCallCountExtractor<T> getObserved() {
		return observed;
	}
	
	protected BaseCallCountExtractor<T> getFiltered() {
		return filtered;
	}
	
	protected void setObserved(final BaseCallCountExtractor<T> observed) {
		this.observed = observed;
	}
	
	protected void setFiltered(final BaseCallCountExtractor<T> filtered) {
		this.filtered = filtered;
	}
	
	protected abstract List<ProcessRecord> createProcessRecord(final RegionDataCache<T> regionDataCache);
	
}
