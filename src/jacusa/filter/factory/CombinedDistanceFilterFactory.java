package jacusa.filter.factory;

import jacusa.filter.UnstrandedFilterContainer;
import jacusa.filter.storage.DistanceStorage;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

public class CombinedDistanceFilterFactory<T extends PileupData> 
extends AbstractDistanceFilterFactory<T> {

	public CombinedDistanceFilterFactory(final AbstractParameter<T> parameters) {
		super('I', "Filter distance to TODO position.", 5, 0.5, 1, parameters);
	}

	public CombinedDistanceFilter<T> getFilter() {
		return new CombinedDistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

	@Override
	public void registerFilter(UnstrandedFilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessRecord(storage); // read position
		filterContainer.registerProcessSkipped(storage); // splice site
		
		// INDEL
		filterContainer.registerProcessInsertion(storage); 
		filterContainer.registerProcessDeletion(storage);

	}
}