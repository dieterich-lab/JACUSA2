package jacusa.filter.factory;

import jacusa.filter.FilterContainer;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount>
extends AbstractDistanceFilterFactory<T, F> {

	public SpliceSiteDistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('S', "Filter distance to Splice Site.", 6, 0.5, 2, dataGenerator);
	}

	public SpliceSiteDistanceFilter<T, F> getFilter() {
		return new SpliceSiteDistanceFilter<T, F>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				this);
	}

	/* (non-Javadoc)
	 * @see jacusa.filter.factory.AbstractFilterFactory#registerFilter(jacusa.filter.FilterContainer)
	 */
	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		/* TODO
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerProcessSkipped(storage);
		*/
	}
	
}