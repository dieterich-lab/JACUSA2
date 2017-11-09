package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;

import lib.data.BaseQualData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class CallWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	final private CallParameters<T> parameters;
	final private StatisticCalculator<T> statisticCalculator;
	
	public CallWorker(
			final WorkerDispatcher<T> workerDispatcher,
			final int threadId, final CallParameters<T> parameters) {
		super(workerDispatcher, threadId, parameters);
		this.statisticCalculator = parameters.getStatisticParameters().getStatisticCalculator();
		this.parameters = parameters;
	}

	@Override
	protected void doWork() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected Result<T> processParallelData(final ParallelData<T> parallelData, 
			final WindowedIterator<T> parallelDataIterator) {
		// result object
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return null;
		}

		if (parameters.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}

		return result;
	}

}
