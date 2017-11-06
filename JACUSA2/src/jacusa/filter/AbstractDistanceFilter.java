package jacusa.filter;

import java.util.List;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.CombinedCountFilter;
import jacusa.filter.storage.AbstractWindowStorage;
import jacusa.pileup.iterator.WindowedIterator;
import jacusa.util.Coordinate;

public abstract class AbstractDistanceFilter<T extends BaseQualData> 
extends AbstractFilter<T> {

	private final int filterDistance;
	private final AbstractCountFilter<T> countFilter;

	
	public AbstractDistanceFilter(final char c, 
			final int filterDistance, final double minRatio, final int minCount,
			final AbstractParameters<T> parameters) {
		super(c);
		this.filterDistance	= filterDistance;
		
		countFilter 	= new CombinedCountFilter<T>(minRatio, minCount, parameters);
	}

	@Override
	protected boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
		final ParallelPileupData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		// get position from result
		final Coordinate coordinate = parallelData.getCoordinate();
		final int genomicPosition = coordinate.getStart();
		final char referenceBase = result.getParellelData().getCombinedPooledData().getReferenceBase();
		
		// create container [condition][replicates]
		final BaseQualData[][] baseQualData = new BaseQualData[parallelData.getConditions()][];
		
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			// filter container per condition
			List<FilterContainer<T>> filterContainers = windowIterator
					.getConditionContainer()
					.getReplicatContainer(conditionIndex)
					.getFilterContainers(coordinate);

			// replicates for condition
			int replicates = filterContainers.size();
			
			// container for replicates of a condition
			BaseQualData[] replicatesData = new BaseQualData[replicates];

			// collect data from each replicate
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// replicate specific filter container
				final FilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
				// filter storage associated with filter and replicate
				final AbstractWindowStorage<T> storage = filterContainer.getWindowStorage(getC());
				// convert genomic to window/storage speficic coordinates
				final int windowPosition = storage.getWindowCache().getWindowCoordinates().convert2WindowPosition(genomicPosition);

				BaseQualData replicateData = new BaseQualData(coordinate, referenceBase, filterContainer.getCondition().getLibraryType());
				replicateData.setBaseQualCount(storage.getWindowCache().getBaseCount(windowPosition).copy());
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
