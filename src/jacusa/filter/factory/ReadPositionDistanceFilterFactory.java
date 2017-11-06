package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;

public class ReadPositionDistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public ReadPositionDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('B', "Filter distance to Read Start/End.", 6, 0.5, 2, parameters);
	}

	public ReadPositionDistanceFilter<T> getFilter() {
		return new ReadPositionDistanceFilter<T>(getC(), 
				getFilterDistance(), getFilterMinRatio(), getFilterMinCount(),
				getParameters());
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerWindowStorage(storage);
		filterContainer.registerProcessRecord(storage);
	}
	
}