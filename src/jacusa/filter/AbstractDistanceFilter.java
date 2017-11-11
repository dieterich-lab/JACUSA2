package jacusa.filter;

import java.util.List;

import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.CombinedCountFilter;
import jacusa.filter.storage.AbstractCacheStorage;
import lib.cli.parameters.AbstractParameter;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.basecall.PileupData;
import lib.data.builder.ConditionContainer;
import lib.util.Coordinate;

public abstract class AbstractDistanceFilter<T extends PileupData> 
extends AbstractFilter<T> {

	private final int filterDistance;
	private final AbstractCountFilter<T> countFilter;

	
	public AbstractDistanceFilter(final char c, 
			final int filterDistance, final double minRatio, final int minCount,
			final AbstractParameter<T> parameters) {
		super(c);
		this.filterDistance	= filterDistance;
		
		countFilter 	= new CombinedCountFilter<T>(minRatio, minCount, parameters);
	}

	@Override
	protected boolean filter(final Result<T> result, final ConditionContainer<T> conditionContainer) {
		final ParallelData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		// get position from result
		final Coordinate coordinate = parallelData.getCoordinate();
		final int genomicPosition = coordinate.getStart();
		final char referenceBase = result.getParellelData().getCombinedPooledData().getReferenceBase();
		
		// create container [condition][replicates]
		final PileupData[][] baseQualData = new PileupData[parallelData.getConditions()][];
		
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			// filter container per condition
			List<UnstrandedFilterContainer<T>> filterContainers = conditionContainer
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
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseQualData);
	}

	public int getFilterDistance() {
		return filterDistance;
	}
	
	@Override
	public int getOverhang() {
		return filterDistance;
	}
	
}
