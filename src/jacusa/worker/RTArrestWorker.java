package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.RTArrestParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.StatisticResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends AbstractData & hasBaseCallCount & hasReferenceBase & hasRTarrestCount>
extends AbstractWorker<T, StatisticResult<T>> {

	private final AbstractStatisticCalculator<T> statisticCalculator;
	
	public RTArrestWorker(final WorkerDispatcher<T, StatisticResult<T>> workerDispatcher,
			final int threadId,
			final CopyTmpResult<T, StatisticResult<T>> copyTmpResult,
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final RTArrestParameter<T> rtArrestParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, rtArrestParameter);
		statisticCalculator = rtArrestParameter
				.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<T> process(final ParallelData<T> parallelData) {
		return statisticCalculator.filter(parallelData);
	}
	
}
