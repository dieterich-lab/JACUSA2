package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.CallParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasPileupCount;
import lib.data.result.StatisticResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class CallWorker<T extends AbstractData & hasPileupCount>
extends AbstractWorker<T, StatisticResult<T>> {

	private final AbstractStatisticCalculator<T> statisticCalculator;

	public CallWorker(
			final WorkerDispatcher<T, StatisticResult<T>> workerDispatcher,
			final int threadId, 
			final CopyTmpResult<T, StatisticResult<T>> copyTmpResult,
			final List<ParallelDataValidator<T>> parallelDataValidators,
			final CallParameter<T> callParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, callParameter);
		this.statisticCalculator = callParameter.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<T> process(final ParallelData<T> parallelData) {
		return statisticCalculator.filter(parallelData);
	}
	
}
