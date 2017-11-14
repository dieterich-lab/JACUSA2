package jacusa.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jacusa.cli.parameters.RTArrestParameter;
import jacusa.data.validator.ParallelDataValidator;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.copytmp.CopyTmpRTArrestResult;
import jacusa.method.call.statistic.StatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractWorker<T> {

	private final RTArrestParameter<T> rtArrestParameter;
	private final StatisticCalculator<T> statisticCalculator;

	private CopyTmpRTArrestResult<T> copyTmpResult;
	private List<CopyTmp> copyTmps;
	
	public RTArrestWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final RTArrestParameter<T> rtArrestParameter) {

		super(workerDispatcher, threadId, parallelDataValidators, rtArrestParameter);
		this.rtArrestParameter = rtArrestParameter;
		statisticCalculator = rtArrestParameter
				.getStatisticParameters().getStatisticCalculator().newInstance();
		
		try {
			copyTmpResult = new CopyTmpRTArrestResult<T>(threadId, rtArrestParameter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		copyTmps = new ArrayList<CopyTmp>(1);
		copyTmps.add(copyTmpResult);
	}

	@Override
	protected void doWork(final ParallelData<T> parallelData) {
		final Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);

		if (statisticCalculator.filter(result.getStatistic())) {
			return;
		}

		if (rtArrestParameter.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T, ?> filterFactory : rtArrestParameter.getFilterConfig().getFilterFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, getConditionContainer());
			}
		}

		try {
			copyTmpResult.addResult(result, rtArrestParameter.getConditionParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}
	
}
