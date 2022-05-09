package lib.stat.estimation.provider;

import java.util.List;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.ParallelData;
import lib.stat.estimation.DefaultEstimationContainer;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.nominal.NominalData;

public class INDELestimationCountProvider implements EstimationContainerProvider {

	public static final int PLUS_INDEX = 0;
	public static final int MINUS_INDEX = 1;

	final DataType<IntegerData> dataType;
	final DataType<IntegerData> dataType2;

	private final int maxIterations;
	private final double pseudoCount;

	public INDELestimationCountProvider(final DataType<IntegerData> dataType, final DataType<IntegerData> dataType2,
			final int maxIterations) {
		this.dataType = dataType;
		this.dataType2 = dataType2;

		this.maxIterations = maxIterations;
		this.pseudoCount = 1d;
	}

	@Override
	public EstimationContainer[] convert(ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final EstimationContainer[] estimationContainers = new EstimationContainer[conditions + 1];

		for (int condI = 0; condI < conditions; ++condI) {
			final NominalData nominalData = createData(parallelData.getData(condI));
			estimationContainers[condI] = createContainer(Integer.toString(condI + 1), nominalData, maxIterations);
		}

		// conditions pooled
		final NominalData nominalData = createData(parallelData.getCombinedData());
		estimationContainers[conditions] = new DefaultEstimationContainer("P", nominalData, maxIterations);
		return estimationContainers;
	}

	private EstimationContainer createContainer(final String id, final NominalData nominalData,
			final int maxIterations) {
		return new DefaultEstimationContainer(id, nominalData, maxIterations);
	}

	public NominalData createData(final List<DataContainer> dataContainers) {
		final int catergories = 2;
		final double[][] dataMatrix = new double[dataContainers.size()][catergories]; // -> 2 because BetaBin
		for (int replicateI = 0; replicateI < dataContainers.size(); replicateI++) {
			final DataContainer container = dataContainers.get(replicateI);
			final int count = getCount(container).getValue();
			final int coverageCount = container.get(dataType2).getValue();
			dataMatrix[replicateI][PLUS_INDEX] = count + pseudoCount;
			dataMatrix[replicateI][MINUS_INDEX] = coverageCount - count + pseudoCount;
		}
		return NominalData.build(catergories, dataMatrix);
	}

	public IntegerData getCount(DataContainer container) {
		return container.get(dataType);
	}
}
