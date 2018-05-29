package jacusa.filter.basecall;

import java.util.List;
import java.util.Set;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.FilterRatio;
import lib.cli.options.Base;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.count.BaseCallCount;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

/**
 * Abstract class that enables filtering based on base call count data and some other filter chached data.
 * 
 * @param <T>
 */
public class BaseCallFilter<T extends AbstractData & HasReferenceBase> 
extends AbstractDataFilter<T> {

	private final BaseCallCountExtractor<T> observed;
	private final BaseCallCountExtractor<T> filtered;

	private final BaseCallCountFilter baseCallCountFilter;

	public BaseCallFilter(final char c, 
			final BaseCallCountExtractor<T> observed,
			final BaseCallCountExtractor<T> filtered,
			final int overhang, 
			final FilterRatio filterRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<RecordWrapperDataCache<T>>> conditionFilterCaches) {

		super(c, overhang, parameter, conditionFilterCaches);
		this.observed = observed;
		this.filtered = filtered;
		baseCallCountFilter 		= new BaseCallCountFilter(filterRatio);
	}

	public static ParallelData<BaseCallData> createBaseCallData(final LIBRARY_TYPE libraryType, 
			final Coordinate coordinate, final byte refBase, final BaseCallCount[][] baseCallCount) {
		
		final DataGenerator<BaseCallData> dataGenerator =  new BaseCallDataGenerator();
		final int conditions = baseCallCount.length;
		final BaseCallData[][] baseCallData = dataGenerator.createContainerData(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for this condition
			final int replicates = baseCallCount[conditionIndex].length;
			baseCallData[conditionIndex] = dataGenerator.createReplicateData(replicates);
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// specific data
				final BaseCallData tmpData = new BaseCallData(libraryType, coordinate, refBase);
				tmpData.getBaseCallCount().add(baseCallCount[conditionIndex][replicateIndex]);
				baseCallData[conditionIndex][replicateIndex] = tmpData;
			}
		}
		return new ParallelData<BaseCallData>(dataGenerator, baseCallData);
	}
	
	protected BaseCallCount[][] createBaseCallCount(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final BaseCallCount[][] baseCallCount = new BaseCallCount[conditions][];
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for this condition
			final int replicates = parallelData.getReplicates(conditionIndex);
			baseCallCount[conditionIndex] = new BaseCallCount[replicates];
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// create new data
				baseCallCount[conditionIndex][replicateIndex] = 
						observed.getBaseCallCount(parallelData.getData(conditionIndex, replicateIndex));
			}
		}
		return baseCallCount;
	}
	
	protected BaseCallCount[][] createFilteredBaseCallCount(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final BaseCallCount[][] baseCallCount = new BaseCallCount[conditions][];
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for this condition
			final int replicates = parallelData.getReplicates(conditionIndex);
			baseCallCount[conditionIndex] = new BaseCallCount[replicates];
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// create new data
				baseCallCount[conditionIndex][replicateIndex] = 
						filtered.getBaseCallCount(parallelData.getData(conditionIndex, replicateIndex));
			}
		}
		return baseCallCount;
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		final BaseCallCount[][] observed = createBaseCallCount(parallelData);
		final BaseCallCount[][] filtered = createFilteredBaseCallCount(parallelData);
		
		final ParallelData<BaseCallData> tmpParallelData = createBaseCallData(
				parallelData.getLibraryType(), 
				parallelData.getCoordinate(),
				parallelData.getCombinedPooledData().getReferenceBase(),
				observed); 
		final Set<Base> variantBases = ParallelData.getVariantBases(tmpParallelData);
		
		return baseCallCountFilter.filter(variantBases, observed, filtered);
	}
	
}
