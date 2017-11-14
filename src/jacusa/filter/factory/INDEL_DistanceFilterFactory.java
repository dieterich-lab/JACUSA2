package jacusa.filter.factory;

import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class INDEL_DistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractDistanceFilterFactory<T, F> {

	public INDEL_DistanceFilterFactory(final DataGenerator<F> dataGenerator) {
		super('I', "Filter distance to INDEL position.", 6, 0.2, 2, dataGenerator);
	}

	public INDEL_DistanceFilter<T, F> getFilter() {
		return new INDEL_DistanceFilter<T, F>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), this);
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());

		// TODO
		final DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), null);
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessInsertion(storage);
		filterContainer.registerProcessDeletion(storage);
	}
	
}