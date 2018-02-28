package jacusa.filter;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jacusa.filter.cache.FilterCache;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.has.hasLinkedReadArrestCount;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.data.result.Result;
import lib.util.coordinate.Coordinate;

public class Ref2BaseCallDataFilter<T extends AbstractData & hasLinkedReadArrestCount, F extends AbstractData & hasLinkedReadArrestCount> 
extends AbstractDataFilter<T, F> {

	private int minCount;
	private double minRatio;
	
	private BaseCallDataGenerator bcDataGenerator;

	public Ref2BaseCallDataFilter(final char c, 
			final int overhang, 
			final int minCount,
			final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final AbstractDataFilterFactory<T, F> filterFactory,
			final List<List<FilterCache<F>>> conditionFilterCaches) {
		
		super(c, overhang, parameter, filterFactory, conditionFilterCaches);
		this.minCount = minCount;
		this.minRatio = minRatio;
		bcDataGenerator = new BaseCallDataGenerator();
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		// coordinate of potential read arrest position
		final Coordinate coordinate = parallelData.getCoordinate();
		
		// number of conditions 
		final int conditions = parallelData.getConditions();
		
		// merged/combined condition and replicate data
		final T combinedPooled = parallelData.getCombinedPooledData();

		// get all reference positions of base substitutions that are linked to the same read arrest site
		final Set<Integer> refPositions = new TreeSet<Integer>(combinedPooled.getLinkedReadArrestCount().getRefPos2baseChange4arrest().keySet());
		
		final boolean[] artefact = new boolean[refPositions.size()];
		// result of method: all linked positions need to be artefacts
		boolean filter = false;
		int refPositionIndex = 0;
		
		for (int refPosition : refPositions) {
			// create new data container to store linked base substitution positions
			final BaseCallData[][] bcData = bcDataGenerator.createContainerData(conditions);
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				// number of replicates for this condition
				final int replicates = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// specific data
					final T tmpData = parallelData.getData(conditionIndex, replicateIndex);
					
					// create new data
					bcData[conditionIndex][replicateIndex] = bcDataGenerator.createData(tmpData.getLibraryType(), coordinate);
					// get base call count from linked position
					BaseCallCount tmpBcCount = tmpData.getLinkedReadArrestCount().getRefPos2baseChange4arrest().get(refPosition);
					// add to new data
					bcData[conditionIndex][replicateIndex].getBaseCallCount().add(tmpBcCount);
				}
			}
			// create new parallel data
			ParallelData<BaseCallData> tmpParallelData = new ParallelData<BaseCallData>(bcDataGenerator, bcData);
			final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(tmpParallelData);

			for (int variantBaseIndex : variantBaseIndexs) {
				int count = 0;
				int filteredCount = 0;

				for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
					for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
						count += tmpParallelData.getData(conditionIndex, replicateIndex).getBaseCallCount().getBaseCallCount(variantBaseIndex);

						final LIBRARY_TYPE libraryFype = tmpParallelData.getData(conditionIndex, replicateIndex).getLibraryType();
						filteredCount += getFilteredData(coordinate, libraryFype, conditionIndex, replicateIndex)
								.getLinkedReadArrestCount()
								.getRefPos2baseChange4arrest()
								.get(refPosition)
								.getBaseCallCount(variantBaseIndex);
					}
				}

				if (filter(count, filteredCount)) {
					artefact[refPositionIndex] = true;
					if (refPositionIndex == 0) {
						filter = true;
					} else {
						filter &= true;
					}
				} else {
					filter = false;
				}
			}

			refPositionIndex++;
		}

		return filter;
	}

	protected boolean filter(final int count, int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio || count - filteredCount >= minCount;
	}

	public void addFilterInfo(Result<T> result) {
		result.getFilterInfo().add(Character.toString(getC()));
	}
	
}
