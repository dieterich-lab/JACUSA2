package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilterFactory<T extends BaseQualData>
extends AbstractDistanceFilterFactory<T> {

	public SpliceSiteDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('S', "Filter distance to Splice Site.", 6, 0.5, 2, parameters);
	}

	public SpliceSiteDistanceFilter<T> getFilter() {
		return new SpliceSiteDistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

	/* (non-Javadoc)
	 * @see jacusa.filter.factory.AbstractFilterFactory#registerFilter(jacusa.filter.FilterContainer)
	 */
	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerProcessSkipped(storage);
	}
	
}