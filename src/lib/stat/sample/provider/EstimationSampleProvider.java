package lib.stat.sample.provider;

import lib.data.ParallelData;
import lib.stat.sample.EstimationSample;

public interface EstimationSampleProvider {

	EstimationSample[] convert(ParallelData parallelData);

}
