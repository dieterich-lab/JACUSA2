package jacusa.filter;

import java.util.List;

import addvariants.data.WindowedIterator;

import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.MinCountFilter;
import jacusa.filter.storage.AbstractWindowStorage;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.util.Coordinate;

public class HomopolymerFilter<T extends BaseQualData> 
extends AbstractFilter<T> {

	private AbstractCountFilter<T> countFilter;

	public HomopolymerFilter(final char c, final int length, final AbstractParameters<T> parameters) {
		super(c);

		countFilter = new MinCountFilter<T>(1, parameters);
	}

	@Override
	protected boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
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

	@Override
	public int getOverhang() {
		return 0;
	}
	
}
