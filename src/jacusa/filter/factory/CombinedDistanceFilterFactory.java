package jacusa.filter.factory;

import jacusa.filter.FilterContainer;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class CombinedDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractDistanceFilterFactory<T, F> {

	public CombinedDistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('I', "Filter distance to TODO position.", 5, 0.5, 1, dataGenerator);
	}
	
	public CombinedDistanceFilter<T, F> getFilter() {
		return new CombinedDistanceFilter<T, F>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				this);
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		/*
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessRecord(storage); // read position
		filterContainer.registerProcessSkipped(storage); // splice site
		
		// INDEL
		filterContainer.registerProcessInsertion(storage); 
		filterContainer.registerProcessDeletion(storage);
		 */
	}
}