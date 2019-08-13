package lib.stat.sample.provider;

import java.util.List;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.stat.nominal.NominalData;
import lib.stat.sample.DefaultEstimationSample;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;

public class InsertionCountSampleProvider implements EstimationSampleProvider {

	public static final int INSERTION_INDEX	= 0;
	public static final int NO_INSERTED_INDEX	= 1;
	
	private final int maxIterations;
	private final double pseudoCount;

	public InsertionCountSampleProvider(final int maxIterations) {
		this.maxIterations 	= maxIterations;
		this.pseudoCount 	= 1d;
	}
	
	@Override
	public EstimationSample[] convert(ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final EstimationSample[] estimationSamples = new EstimationSample[conditions + 1];
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final NominalData nominalData 		= createData(parallelData.getData(conditionIndex)); 
			estimationSamples[conditionIndex] 	= createSample(Integer.toString(conditionIndex + 1), nominalData, maxIterations);
		}

		// conditions pooled
		final NominalData nominalData = createData(parallelData.getCombinedData());
		estimationSamples[conditions] = new DefaultEstimationSample("P", nominalData, maxIterations);
		return estimationSamples;
	}

	private EstimationSample createSample(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultEstimationSample(id, nominalData, maxIterations);
	}
	
	public NominalData createData(final List<DataContainer> dataContainers) {
		final int catergories = 2;
		final double[][] dataMatrix  = new double[dataContainers.size()][catergories]; // -> 2 because BetaBin
		for (int replicateIndex = 0; replicateIndex < dataContainers.size(); replicateIndex++) {
			final DataContainer dataContainer = dataContainers.get(replicateIndex);
			final int insertionCount 	= dataContainer.getInsertionCount().getValue();
			final int coverageCount 	= dataContainer.getCoverage().getValue();
			dataMatrix[replicateIndex][INSERTION_INDEX] 		= insertionCount + pseudoCount;
			dataMatrix[replicateIndex][NO_INSERTED_INDEX]	= coverageCount - insertionCount + pseudoCount;
		}
		return NominalData.build(catergories, dataMatrix);
	}
	
}
