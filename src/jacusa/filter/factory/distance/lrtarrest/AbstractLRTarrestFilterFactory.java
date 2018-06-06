package jacusa.filter.factory.distance.lrtarrest;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.LRTarrestRef2BaseCallFilter;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.extractor.lrtarrest.RefPos2BaseCallCountExtractor;
import lib.data.cache.extractor.lrtarrest.RefPos2BaseCallCountFilterDataExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;
import lib.io.ResultWriterUtils;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractLRTarrestFilterFactory<T extends AbstractData & HasRefPos2BaseCallCountFilterData & HasReferenceBase>
extends AbstractDataFilterFactory<T> {

	private RefPos2BaseCallCountExtractor<T> observed;
	private RefPos2BaseCallCountExtractor<T> filtered;
	
	private int filterDistance;
	private double filterMinRatio;
		
	public AbstractLRTarrestFilterFactory(final Option option,
			final RefPos2BaseCallCountExtractor<T> observed, final RefPos2BaseCallCountExtractor<T> filtered, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option);

		this.observed = observed;
		this.filtered = filtered;
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
	}
	
	public AbstractLRTarrestFilterFactory(final Option option,
			final RefPos2BaseCallCountExtractor<T> observed, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {
		
		this(option, 
				observed, 
				new RefPos2BaseCallCountFilterDataExtractor<T>(option.getOpt().charAt(0)), 
				defaultFilterDistance, defaultFilterMinRatio);
	}
	
	@Override
	protected Options getOptions() {
		final Options options = new Options();

		options.addOption(Option.builder("distance")
				.hasArg(true)
				.desc("Filter base calls within distance to feature. Default: " + getDistance())
				.build());

		options.addOption(Option.builder("minRatio")
				.hasArg(true)
				.desc("Minimal ratio of base calls to pass filtering. Default: " + getMinRatio())
				.build());
		
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		// format D:distance:minRatio
		// :minCount

		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "distance":
				filterDistance = Integer.parseInt(cmd.getOptionValue(longOpt));
				break;
				
			case "minRatio":
				filterMinRatio = Double.parseDouble(cmd.getOptionValue(longOpt));
				break;

			default:
				break;
			}
		}
	}

	@Override
	public AbstractFilter<T> createFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		return new LRTarrestRef2BaseCallFilter<T>(getC(),
				getObserved(),
				getFiltered(),	
				getDistance(), new FilterRatio(getMinRatio()));
	}

	@Override
	public RecordWrapperDataCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final CoordinateController coordinateController) {

		/* FIXME
		// save classes
		final UniqueTraverse<T> uniqueBaseCallCache = 
			new UniqueTraverse<T>(
					new LRTarrest2BaseCallCountDataCache<T>(
							null, getFiltered(), null,
							conditionParameter.getLibraryType(), conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), 
							baseCallConfig, 
							coordinateController));

		return new RecordProcessDataCache<T>(uniqueBaseCallCache, createProcessRecord(uniqueBaseCallCache));
		*/
		return null;
	}

	
	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		ResultWriterUtils.addResultRefPos2baseChange(sb, getFiltered().getRefPos2BaseCallCountExtractor(data));
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

	protected RefPos2BaseCallCountExtractor<T> getObserved() {
		return observed;
	}
	
	protected RefPos2BaseCallCountExtractor<T> getFiltered() {
		return filtered;
	}
	
	protected void setObserved(final RefPos2BaseCallCountExtractor<T> observed) {
		this.observed = observed;
	}
	
	protected void setFiltered(final RefPos2BaseCallCountExtractor<T> filtered) {
		this.filtered = filtered;
	}
	
	protected abstract List<ProcessRecord> createProcessRecord(final RegionDataCache<T> regionDataCache);
	
}
