package jacusa.filter.factory.distance.lrtarrest;

import java.util.List;

import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.LRTarrestRef2BaseCallDataFilter;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractDataFilterFactory;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
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
		
	public AbstractLRTarrestFilterFactory(final char c, final String desc,
			final RefPos2BaseCallCountExtractor<T> observed, final RefPos2BaseCallCountExtractor<T> filtered, 
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
	
	public AbstractLRTarrestFilterFactory(final char c, final String desc,
			final RefPos2BaseCallCountExtractor<T> observed, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {
		
		this(c, desc, observed, new RefPos2BaseCallCountFilterDataExtractor<T>(c), defaultFilterDistance, defaultFilterMinRatio);
	}
	
	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));

		// format D:distance:minRatio
		// :minCount
		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int filterDistance = Integer.valueOf(s[i]);
				if (filterDistance < 0) {
					throw new IllegalArgumentException("Invalid distance " + line);
				}
				this.filterDistance = filterDistance;
				break;

			case 2:
				final double filterMinRatio = Double.valueOf(s[i]);
				if (filterMinRatio < 0.0 || filterMinRatio > 1.0) {
					throw new IllegalArgumentException("Invalid minRatio " + line);
				}
				this.filterMinRatio = filterMinRatio;
				break;

			/*
			case 3:
				final int filterMinCount = Integer.valueOf(s[i]);
				if (filterMinCount < 0) {
					throw new IllegalArgumentException("Invalid minCount " + line);
				}
				this.filterMinCount = filterMinCount;
				break;
				*/
				
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<RecordWrapperDataCache<T>>> conditionFilterCaches = 
				createConditionFilterCaches(parameter, coordinateController, this);
		
		final LRTarrestRef2BaseCallDataFilter<T> dataFilter = new LRTarrestRef2BaseCallDataFilter<T>(getC(),
				getObserved(),
				getFiltered(),	
				getDistance(), new FilterRatio(getMinRatio()), 
				parameter, conditionFilterCaches);

		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}

	@Override
	protected RecordWrapperDataCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
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
