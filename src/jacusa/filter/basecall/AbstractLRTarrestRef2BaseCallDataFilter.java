package jacusa.filter.basecall;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.util.coordinate.Coordinate;

/**
 * 
 * @param <T>
 */
public abstract class AbstractLRTarrestRef2BaseCallDataFilter<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractDataFilter<T> {

	public static final char SEP = ',';

	private final BaseCallCountFilter<BaseCallData> baseCallCountFilter;

	// container for artefacts
	private final Set<Integer> filteredRefPositions;

	public AbstractLRTarrestRef2BaseCallDataFilter(final char c, 
			final int overhang, 
			final BaseCallCountFilter<BaseCallData> baseCallCountFilter,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang, parameter, conditionFilterCaches);
		this.baseCallCountFilter = baseCallCountFilter;
		filteredRefPositions = new HashSet<Integer>(10);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		// clear buffer
		filteredRefPositions.clear();
		
		// merged/combined condition and replicate data
		final T combinedPooled = parallelData.getCombinedPooledData();

		// get all reference positions of base substitutions 
		// that are linked to the same read arrest site
		final Set<Integer> refPositions = new TreeSet<Integer>(combinedPooled.getLRTarrestCount().getRefPos2bc4arrest().keySet());
		
		final boolean[] artefact = new boolean[refPositions.size()];
		// result of method: all linked positions need to be artefacts
		boolean filter = false;
		int refPositionIndex = 0;
		
		for (int refPosition : refPositions) {
			final byte refBase = combinedPooled.getLRTarrestCount().getReference().get(refPosition);

			// create new parallel data
			final ParallelData<BaseCallData> tmpParallelData = createParallelData(parallelData, refPosition, refBase);
			final ParallelData<BaseCallData> filteredParallelData = createFilteredParallelData(parallelData, refPosition, refBase);
			if (baseCallCountFilter.filter(tmpParallelData, filteredParallelData)) {
				artefact[refPositionIndex] = true;
				// add to buffer
				filteredRefPositions.add(refPosition);
				filter = true;
			}

			refPositionIndex++;
		}
		
		return filter;
	}

	private ParallelData<BaseCallData> createParallelData(final ParallelData<T> parallelData, final int refPosition, final byte refBase) {
		final Coordinate tmpCoordinate = new Coordinate(parallelData.getCoordinate());
		tmpCoordinate.setPosition(refPosition);
		
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
				BaseCallCount tmpBcCount = tmpData.getLRTarrestCount().getRefPos2bc4arrest().get(refPosition);
				if (tmpBcCount != null) {
					// add to new data
					baseCallData[conditionIndex][replicateIndex].getBaseCallCount().add(tmpBcCount);
				}
			}
		}
		return new ParallelData<BaseCallData>(dataGenerator, baseCallData);
	}
	
	private ParallelData<BaseCallData> createFilteredParallelData(final ParallelData<T> parallelData, final int refPosition, final byte refBase) {
		final Coordinate tmpCoordinate = new Coordinate(parallelData.getCoordinate());
		tmpCoordinate.setPosition(refPosition);

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
				final Map<Integer, BaseCallCount> filtered = getFilteredData(parallelData, conditionIndex, replicateIndex);
				if (filtered != null && filtered.containsKey(refPosition)) {
					baseCallData[conditionIndex][replicateIndex].getBaseCallCount().add(filtered.get(refPosition));
				}
			}
		}
		return new ParallelData<BaseCallData>(dataGenerator, baseCallData);
	}
	
	protected abstract Map<Integer, BaseCallCount> getFilteredData(
			ParallelData<T> parallelData, int conditionIndex, int replicateIndex);

	@Override
	public void addInfo(Result<T> result) {
		final String value = StringUtil.join(Character.toString(SEP), filteredRefPositions);
		// add position of artefact(s) to unique char id
		result.getFilterInfo().add(Character.toString(getC()), value);
	}

}
