package lib.stat.estimation.provider;

import lib.data.ParallelData;
import lib.stat.estimation.EstimationContainer;

public interface ConditionEstimateProvider {

	EstimationContainer convert(ParallelData parallelData);

}
