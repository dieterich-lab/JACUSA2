package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;

public class CombinedDistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public CombinedDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('I', "Filter distance to TODO position.", 5, 0.5, 1, parameters);
	}

	public CombinedDistanceFilter<T> getFilter() {
		return new CombinedDistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		
		filterContainer.registerWindowStorage(storage);
		filterContainer.registerProcessRecord(storage); // read position
		filterContainer.registerProcessSkipped(storage); // splice site
		
		// INDEL
		filterContainer.registerProcessInsertion(storage); 
		filterContainer.registerProcessDeletion(storage);

	}
}