package jacusa.filter;

import jacusa.filter.counts.AbstractBaseCallCountFilter;
import jacusa.filter.counts.MinCountFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.builder.ConditionContainer;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class HomopolymerFilter<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractFilter<T> {

	private AbstractBaseCallCountFilter<T, F> countFilter;

	public HomopolymerFilter(final char c, final int length, final AbstractFilterFactory<T, F> filterFactory) {
		super(c);

		countFilter = new MinCountFilter<T, F>(1, filterFactory);
	}

	@Override
	protected boolean filter(final Result<T> result, final ConditionContainer<T> conditionContainer) {
		final ParallelData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		/* TODO
		// get position from result
		final Coordinate coordinate = parallelData.getCoordinate();
		final int genomicPosition = coordinate.getStart();
		final byte referenceBase = result.getParellelData().getCombinedPooledData().getReferenceBase();
		*/

		final ParallelData<F> parallelFilteredData = null;
		/* TODO
		 * 
		// create container [condition][replicates]
		final PileupData[][] baseQualData = new PileupData[parallelData.getConditions()][];
		
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			// filter container per condition
			List<UnstrandedFilterContainer<T>> filterContainers = windowIterator
					.getConditionContainer()
					.getReplicatContainer(conditionIndex)
					.getFilterContainers(coordinate);

			// replicates for condition
			int replicates = filterContainers.size();
			
			// container for replicates of a condition
			PileupData[] replicatesData = new PileupData[replicates];

			// collect data from each replicate
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// replicate specific filter container
				final UnstrandedFilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
				// filter storage associated with filter and replicate
				final AbstractCacheStorage<T> storage = filterContainer.getStorage(getC());
				// convert genomic to window/storage speficic coordinates
				final int windowPosition = storage.getBaseCallCache().getWindowCoordinates().convert2WindowPosition(genomicPosition);

				PileupData replicateData = new PileupData(coordinate, referenceBase, filterContainer.getCondition().getLibraryType());
				replicateData.setBaseQualCount(storage.getBaseCallCache().getBaseCallCount(windowPosition).copy());
				replicatesData[replicateIndex] = replicateData;
			}
		}
		*/
		
		return countFilter.filter(variantBaseIndexs, parallelData, parallelFilteredData);
	}

	@Override
	public int getOverhang() {
		return 0;
	}
	
}
