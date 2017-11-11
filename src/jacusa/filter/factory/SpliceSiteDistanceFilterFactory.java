package jacusa.filter.factory;

import jacusa.filter.UnstrandedFilterContainer;
import jacusa.filter.storage.DistanceStorage;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilterFactory<T extends PileupData>
extends AbstractDistanceFilterFactory<T> {

	public SpliceSiteDistanceFilterFactory(final AbstractParameter<T> parameters) {
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
	public void registerFilter(UnstrandedFilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerProcessSkipped(storage);
	}
	
}