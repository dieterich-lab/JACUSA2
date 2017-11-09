package jacusa.pileup.worker;

import java.util.List;

import jacusa.cli.parameters.CallParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.iterator.variant.ParallelDataValidator;

import lib.data.ParallelData;
import lib.data.Result;
import lib.data.basecall.PileupData;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class CallWorker<T extends PileupData> 
extends AbstractWorker<T> {

	final private CallParameters<T> callParameter;
	final private StatisticCalculator<T> statisticCalculator;
	
	public CallWorker(
			final WorkerDispatcher<T> workerDispatcher,
			final int threadId, final List<CopyTmp> copyTmps, 
			final ParallelDataValidator<T> parallelDataValidator,
			final CallParameters<T> callParameter) {

		super(workerDispatcher, threadId, copyTmps, parallelDataValidator, callParameter);
		this.statisticCalculator = callParameter.getStatisticParameters().getStatisticCalculator();
		this.callParameter = callParameter;
	}

	@Override
	protected void doWork(ParallelData<T> parallelData) {
		/* TODO
		// result object
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return null;
		}

		if (callParameter.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : callParameter.getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}
		*/
	}

}
