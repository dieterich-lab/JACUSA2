package jacusa.filter.factory.basecall;

import java.util.List;

import jacusa.filter.FilterRatio;
import jacusa.filter.basecall.BaseCallFilter;
import jacusa.filter.cache.RecordProcessDataCache;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.AbstractDataFilterFactory;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.BaseCallCountFilterDataExtractor;
import lib.data.cache.record.RecordDataCache;
import lib.data.cache.region.ArrayBaseCallRegionDataCache;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.UniqueTraverse;
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
		
		final List<List<RecordDataCache<T>>> conditionFilterCaches = 
				createConditionFilterCaches(parameter, coordinateController, this);
		
		final BaseCallFilter<T> dataFilter = new BaseCallFilter<T>(getC(),
				getObserved(),
				getFiltered(),	
				getDistance(), new FilterRatio(getMinRatio()), 
				parameter, conditionFilterCaches);

		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}

	@Override
	protected RecordDataCache<T> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final CoordinateController coordinateController) {

		// save classes
		final UniqueTraverse<T> uniqueBaseCallCache = 
			new UniqueTraverse<T>(
					new ArrayBaseCallRegionDataCache<T>(
							getFiltered(), 
							conditionParameter.getMaxDepth(), conditionParameter.getMinBASQ(), 
							coordinateController));

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
