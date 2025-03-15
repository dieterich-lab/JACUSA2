package lib.stat.estimation.provider;

import java.util.List;

import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.data.ParallelData;
import lib.stat.estimation.DefaultEstimationContainer;
import lib.stat.estimation.EstimationContainer;
import lib.stat.nominal.NominalData;

public abstract class INDELestimationCountProvider implements EstimationContainerProvider {

	public static final int PLUS_INDEX	= 0;
	public static final int MINUS_INDEX	= 1;
	
	private final int maxIterations;
	private final double pseudoCount;

	public INDELestimationCountProvider(final int maxIterations) {
		this.maxIterations 	= maxIterations;
		this.pseudoCount 	= 1d;
	}
	
	@Override
	public EstimationContainer[] convert(ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final EstimationContainer[] estimationContainers = 
				new EstimationContainer[conditions + 1];
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final NominalData nominalData 			= 
					createData(parallelData.getData(conditionIndex)); 
			estimationContainers[conditionIndex] 	= 
					createContainer(Integer.toString(conditionIndex + 1), nominalData, maxIterations);
		}

		// conditions pooled
		final NominalData nominalData 		= createData(parallelData.getCombinedData());
		estimationContainers[conditions] 	= new DefaultEstimationContainer("P", nominalData, maxIterations);
		return estimationContainers;
	}

	private EstimationContainer createContainer(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultEstimationContainer(id, nominalData, maxIterations);
	}
	
	// TODO check
	public NominalData createData(final List<DataContainer> dataContainers) {
		final int catergories = 2;
		final double[][] dataMatrix  = new double[dataContainers.size()][catergories]; // -> 2 because BetaBin
		for (int replicateIndex = 0; replicateIndex < dataContainers.size(); replicateIndex++) {
			final DataContainer container 	= dataContainers.get(replicateIndex);
			final int count 				= getCount(container).getValue();
			final int coverageCount 		= container.getPileupCount().getReads(); // TODO container.getBaseCallCount().getValue();
			dataMatrix[replicateIndex][PLUS_INDEX] 	= count + pseudoCount;
			dataMatrix[replicateIndex][MINUS_INDEX]	= coverageCount - count + pseudoCount;
		}
		return NominalData.build(catergories, dataMatrix);
	}
	
	abstract IntegerData getCount(DataContainer container);
}
