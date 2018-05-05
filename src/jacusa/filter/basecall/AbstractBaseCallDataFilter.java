package jacusa.filter.basecall;

import java.util.List;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.FilterRatio;
import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;

/**
 * Abstract class that enables filtering based on base call count data and some other filter chached data.
 * 
 * @param <T>
 */
public abstract class AbstractBaseCallDataFilter<T extends AbstractData & HasBaseCallCount & HasReferenceBase> 
extends AbstractDataFilter<T> {

	private final BaseCallCountFilter<T> baseCallCountFilter;

	public AbstractBaseCallDataFilter(final char c, 
			final int overhang, 
			final FilterRatio filterRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang, parameter, conditionFilterCaches);
		this.baseCallCountFilter = new BaseCallCountFilter<T>(filterRatio);
	}
	
	protected ParallelData<BaseCallData> createFilteredParallelData(final ParallelData<T> parallelData) {
		final Coordinate tmpCoordinate = new Coordinate(parallelData.getCoordinate());
		final byte refBase = parallelData.getCombinedPooledData().getReferenceBase();

		final DataGenerator<BaseCallData> dataGenerator =  new BaseCallDataGenerator();
		final int conditions = parallelData.getConditions();
		final BaseCallData[][] baseCallData = dataGenerator.createContainerData(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for this condition
			final int replicates = parallelData.getReplicates(conditionIndex);
			baseCallData[conditionIndex] = dataGenerator.createReplicateData(replicates);
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// specific data
				final T tmpData = parallelData.getData(conditionIndex, replicateIndex);
				// create new data
				baseCallData[conditionIndex][replicateIndex] = dataGenerator.createData(tmpData.getLibraryType(), tmpCoordinate);
				// set reference base
				baseCallData[conditionIndex][replicateIndex].setReferenceBase(refBase);
				// get base call count from linked position
				BaseCallCount tmpBcCount = getBaseCallFilterCount(parallelData, conditionIndex, replicateIndex);
				if (tmpBcCount != null) {
					// add to new data
					baseCallData[conditionIndex][replicateIndex].getBaseCallCount().add(tmpBcCount);
				}
			}
		}
		return new ParallelData<BaseCallData>(dataGenerator, baseCallData);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		return baseCallCountFilter.filter(parallelData, createFilteredParallelData(parallelData));
	}

	/*
	public abstract BaseCallCount getBaseCallCount(ParallelData<T> parallelData, 
			int conditionIndex, int replicateIndex);
			*/
	
	/**
	 * Returns a BaseCallCount object for a specific condition and replicate.
	 * 
	 * @param parallelData		the data to extract the BaseCallCount object from 
	 * @param conditionIndex	the condition
	 * @param replicateIndex	the replicate
	 * @return a BaseCallCount object
	 */
	public abstract BaseCallCount getBaseCallFilterCount(ParallelData<T> parallelData, 
			int conditionIndex, int replicateIndex);
	
}
