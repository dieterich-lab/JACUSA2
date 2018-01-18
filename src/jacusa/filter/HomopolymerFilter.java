package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasHomopolymerInfo;
import lib.data.has.hasReferenceBase;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class HomopolymerFilter<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasHomopolymerInfo> 
extends AbstractDataFilter<T, F> {

	public HomopolymerFilter(final char c, 
			final int minPolymerLength, 
			final AbstractParameter<T, ?> parameter,
			final AbstractDataFilterFactory<T, F> dataFilterFactory, 
			final List<List<FilterCache<F>>> conditionFilterCaches) {
		
		super(c, minPolymerLength, parameter, dataFilterFactory, conditionFilterCaches);
	}
	
	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		final Coordinate coordinate = parallelData.getCoordinate();
		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);
		
		for (int variantBaseIndex : variantBaseIndexs) {
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
					final BaseCallCount baseCallCount = parallelData.getData(conditionIndex, replicateIndex).getBaseCallCount();
					if (baseCallCount.getBaseCallCount(variantBaseIndex) > 0) {
						final LIBRARY_TYPE libraryFype = parallelData.getData(conditionIndex, replicateIndex).getLibraryType();
						if (getFilteredData(coordinate, libraryFype, conditionIndex, replicateIndex).isHomopolymer()) {
							return true;
						}	
					}
				}
			}
		}
		
		return false;
	}
	
}
