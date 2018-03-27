package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.RTarrestParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.ParallelData;
import lib.data.RTarrestData;
import lib.data.result.StatisticResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker
extends AbstractWorker<RTarrestData, StatisticResult<RTarrestData>> {

	private final AbstractStatisticCalculator<RTarrestData> statisticCalculator;
	
	public RTArrestWorker(final WorkerDispatcher<RTarrestData, StatisticResult<RTarrestData>> workerDispatcher,
			final int threadId,
			final CopyTmpResult<RTarrestData, StatisticResult<RTarrestData>> copyTmpResult,
			final List<ParallelDataValidator<RTarrestData>> parallelDataValidators, 
			final RTarrestParameter rtArrestParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, rtArrestParameter);
		statisticCalculator = rtArrestParameter
				.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<RTarrestData> process(final ParallelData<RTarrestData> parallelData) {
		return statisticCalculator.filter(parallelData);
	}
	
}
