package lib.stat.estimation.provider;

import java.util.List;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.stat.estimation.ConditionEstimate;
import lib.stat.estimation.DefaultConditionEstimate;
import lib.stat.estimation.EstimationContainer;
import lib.stat.nominal.NominalData;

public abstract class INDELestimateProvider implements ConditionEstimateProvider {

	public static final int PLUS_INDEX	= 0;
	public static final int MINUS_INDEX	= 1;
	public static final int CATEGORIES 	= 2;
	
	private final int maxIterations;
	private final double pseudoCount; // TODO other pseudoCount

	public INDELestimateProvider(final int maxIterations) {
		this.maxIterations 	= maxIterations;
		this.pseudoCount 	= 1d;
	}
	
	@Override
	public EstimationContainer convert(ParallelData parallelData) {
		final ConditionEstimate[] conditionEstimates = new ConditionEstimate[parallelData.getConditions()];
		for (int conditionIndex = 0; conditionIndex < conditionEstimates.length; ++conditionIndex) {
			final NominalData nominalData = createData(parallelData.getData(conditionIndex)); 
			conditionEstimates[conditionIndex] = createContainer(
					Integer.toString(conditionIndex + 1),
					nominalData,
					maxIterations);
		}

		return new EstimationContainer(
				conditionEstimates,
				createContainer("P", createData(parallelData.getCombinedData()), maxIterations));
	}

	private ConditionEstimate createContainer(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultConditionEstimate(id, nominalData, maxIterations);
	}
	
	// TODO check
	public NominalData createData(final List<DataContainer> dataContainers) {
		final double[][] dataMatrix  = new double[dataContainers.size()][CATEGORIES]; // -> 2 because BetaBin
		for (int replicateIndex = 0; replicateIndex < dataContainers.size(); replicateIndex++) {
			final DataContainer container 	= dataContainers.get(replicateIndex);
			final int count 				= getCount(container);
			final int coverageCount 		= container.getPileupCount().getReads(); // TODO container.getBaseCallCount().getValue();
			dataMatrix[replicateIndex][PLUS_INDEX] 	= count + pseudoCount;
			dataMatrix[replicateIndex][MINUS_INDEX]	= coverageCount - count + pseudoCount;
		}
		return NominalData.build(CATEGORIES, dataMatrix);
	}
	
	abstract int getCount(DataContainer container);
	
	
}
