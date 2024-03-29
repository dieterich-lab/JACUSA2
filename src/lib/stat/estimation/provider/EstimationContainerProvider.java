package lib.stat.estimation.provider;

import lib.data.ParallelData;
import lib.stat.estimation.EstimationContainer;

public interface EstimationContainerProvider {

	EstimationContainer[] convert(ParallelData parallelData);

}
