package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.CallParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;

import lib.data.CallData;
import lib.data.ParallelData;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.result.StatisticResult;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class CallWorker
extends AbstractWorker<CallData, StatisticResult<CallData>> {

	private final double threshold;
	private final AbstractStatisticCalculator<CallData> statisticCalculator;

	public CallWorker(
			final ReferenceSetter<CallData> referenceSetter,
			final WorkerDispatcher<CallData, StatisticResult<CallData>> workerDispatcher,
			final int threadId, 
			final CopyTmpResult<CallData, StatisticResult<CallData>> copyTmpResult,
			final List<ParallelDataValidator<CallData>> parallelDataValidators,
			final CallParameter callParameter) {

		super(referenceSetter, workerDispatcher, threadId, copyTmpResult, parallelDataValidators, callParameter);
		threshold = callParameter.getStatisticParameters().getThreshold();
		statisticCalculator = callParameter.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<CallData> process(final ParallelData<CallData> parallelData) {
		return statisticCalculator.filter(threshold, parallelData);
	}

}
