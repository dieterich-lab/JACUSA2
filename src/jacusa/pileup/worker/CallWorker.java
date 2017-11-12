package jacusa.pileup.worker;

import java.util.List;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.iterator.variant.ParallelDataValidator;

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
	
	public CallWorker(
			final WorkerDispatcher<T> workerDispatcher,
			final List<CopyTmp> copyTmps, 
			final ParallelDataValidator<T> parallelDataValidator,
			final CallParameter<T> callParameter) {

		super(workerDispatcher, copyTmps, parallelDataValidator, callParameter);
		this.statisticCalculator = callParameter.getStatisticParameters().getStatisticCalculator();
		this.callParameter = callParameter;
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
	}

}
