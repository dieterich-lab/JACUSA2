package jacusa.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jacusa.cli.parameters.CallParameter;
import jacusa.data.validator.ParallelDataValidator;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.copytmp.CopyTmpResult;
import jacusa.method.call.statistic.StatisticCalculator;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasPileupCount;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class CallWorker<T extends AbstractData & hasPileupCount>
extends AbstractWorker<T> {

	private final CallParameter<T> callParameter;
	private final StatisticCalculator<T> statisticCalculator;

	private CopyTmpResult<T> copyTmpResult;
	private List<CopyTmp> copyTmps;
	
	public CallWorker(
			final WorkerDispatcher<T> workerDispatcher,
			final ParallelDataValidator<T> parallelDataValidator,
			final CallParameter<T> callParameter) {

		super(workerDispatcher, parallelDataValidator, callParameter);
		this.statisticCalculator = callParameter.getStatisticParameters().getStatisticCalculator();
		this.callParameter = callParameter;
		
		try {
			copyTmpResult = new CopyTmpResult<T>(getThreadIdContainer().getThreadId(), callParameter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		copyTmps = new ArrayList<CopyTmp>(1);
		copyTmps.add(copyTmpResult);
	}

	@Override
	protected void doWork(final ParallelData<T> parallelData) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);

		if (statisticCalculator.filter(result.getStatistic())) {
			return;
		}

		if (callParameter.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T, ?> filterFactory : callParameter.getFilterConfig().getFilterFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, getConditionContainer());
			}
		}
		
		try {
			copyTmpResult.addResult(result, callParameter.getConditionParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}
	
}
