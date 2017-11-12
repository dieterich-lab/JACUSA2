package jacusa.filter.factory;

import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class ReadPositionDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractDistanceFilterFactory<T, F> {

	public ReadPositionDistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('B', "Filter distance to Read Start/End.", 6, 0.5, 2, dataGenerator);
	}

	public ReadPositionDistanceFilter<T, F> getFilter() {
		return new ReadPositionDistanceFilter<T, F>(getC(), 
				getFilterDistance(), getFilterMinRatio(), getFilterMinCount(),
				this);
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		// TODO
		final DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), null);
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessRecord(storage);
	}
	
}