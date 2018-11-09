package lib.data.result;

import lib.data.ParallelData;

public final class ResultFactory {

	private ResultFactory() {
		throw new AssertionError();
	}
	
	/*
	public static Result createResult(final ParallelData parallelData) {
		return createResult(1, parallelData);
	}
	
	public static Result createResult(final int values, final ParallelData parallelData) {
		if (values == 1) {
			return new OneValueResult(parallelData);
		} else if (values > 1) {
			return new MultiValueResult(values, parallelData);
		} else {
			throw new IllegalArgumentException("Values cannot to < 1 :" + values);
		}
	}
	*/
	
	public static Result createStatResult(final double statValue, final ParallelData parallelData) {
		return createStatResult(new double[] {statValue}, parallelData);
	}
	
	public static Result createStatResult(final double[] statValue, final ParallelData parallelData) {
		if (statValue.length == 1) {
			return new OneStatResult(statValue[0], parallelData);
		} else if (statValue.length > 1) {
			return new MultiStatResult(statValue, parallelData);
		} else {
			throw new IllegalArgumentException("Values size of stat cannot be < 1 :" + statValue);
		}
	}
	
}
