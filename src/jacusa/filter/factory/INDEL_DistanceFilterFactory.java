package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.DistanceStorage;

public class INDEL_DistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public INDEL_DistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('I', "Filter distance to INDEL position.", 6, 0.2, 2, parameters);
	}

	public INDEL_DistanceFilter<T> getFilter() {
		return new INDEL_DistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerWindowStorage(storage);
		filterContainer.registerProcessInsertion(storage);
		filterContainer.registerProcessDeletion(storage);
	}
	
}