package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.LinkageRTArrestParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasReadInfoExtendedCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.StatisticResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class LinkageRTArrestWorker<T extends AbstractData & hasReferenceBase & hasReadInfoExtendedCount>
extends AbstractWorker<T, StatisticResult<T>> {

	private final AbstractStatisticCalculator<T> statisticCalculator;
	
	public LinkageRTArrestWorker(final WorkerDispatcher<T, StatisticResult<T>> workerDispatcher,
			final int threadId,
			final CopyTmpResult<T, StatisticResult<T>> copyTmpResult,
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final LinkageRTArrestParameter<T> lrtArrestParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, lrtArrestParameter);
		statisticCalculator = lrtArrestParameter
				.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<T> process(final ParallelData<T> parallelData) {
		return statisticCalculator.filter(parallelData);
	}
	
}
