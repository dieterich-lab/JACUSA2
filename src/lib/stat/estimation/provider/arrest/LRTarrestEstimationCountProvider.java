package lib.stat.estimation.provider.arrest;

import java.util.List;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.stat.estimation.DefaultEstimationContainer;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.nominal.NominalData;

public class LRTarrestEstimationCountProvider implements EstimationContainerProvider {

	public static final int READ_ARREST_INDEX	= 0;
	public static final int READ_THROUGH_INDEX	= 1;

	private final int maxIterations;
	private final double pseudoCount;

	public LRTarrestEstimationCountProvider(final int maxIterations) {
		this.maxIterations 	= maxIterations;
		this.pseudoCount 	= 1d;
	}
	
	@Override
	public EstimationContainer[] convert(ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final EstimationContainer[] estContainers = new EstimationContainer[conditions + 1];
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final NominalData nominalData 	= createData(parallelData.getData(conditionIndex)); 
			estContainers[conditionIndex] 	= createContainer(Integer.toString(conditionIndex + 1), nominalData, maxIterations);
		}

		// conditions pooled
		final NominalData nominalData = createData(parallelData.getCombinedData());
		estContainers[conditions] = new DefaultEstimationContainer("P", nominalData, maxIterations);
		return estContainers;
	}

	private EstimationContainer createContainer(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultEstimationContainer(id, nominalData, maxIterations);
	}
	
	public NominalData createData(final List<DataContainer> dataContainers) {
		final int catergories = 2;
		final double[][] dataMatrix  = new double[dataContainers.size()][catergories]; // -> 2 because BetaBin
		for (int replicateIndex = 0; replicateIndex < dataContainers.size(); replicateIndex++) {
			final DataContainer dataContainer = dataContainers.get(replicateIndex);
			final int onePosition 		= dataContainer.getCoordinate().get1Position();
			final int readArrestCount 	= dataContainer.getArrestPos2BCC()
					.getArrestBCC(onePosition).getCoverage();
			final int readThroughCount 	= dataContainer.getArrestPos2BCC()
					.getTotalBCC().getCoverage() - readArrestCount;
			dataMatrix[replicateIndex][READ_ARREST_INDEX] 	= readArrestCount + pseudoCount;
			dataMatrix[replicateIndex][READ_THROUGH_INDEX] 	= readThroughCount + pseudoCount;
			
		}
		return NominalData.build(catergories, dataMatrix);
	}
	
}
