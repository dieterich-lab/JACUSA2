package jacusa.pileup.worker;

import java.util.List;

import jacusa.cli.parameters.RTArrestParameters;
import jacusa.method.call.statistic.StatisticCalculator;
import lib.data.PileupReadInfoData;
import lib.data.ParallelData;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends PileupReadInfoData>
extends AbstractWorker<T> {

	private StatisticCalculator<T> statisticCalculator;

	public RTArrestWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId, final List<CopyTmp> copyTmps,
			final RTArrestParameters<T> rtArrestParameter) {

		super(workerDispatcher, threadId, copyTmps, null, rtArrestParameter);
		statisticCalculator = rtArrestParameter
				.getStatisticParameters().getStatisticCalculator().newInstance();
	}

	@Override
	protected void doWork(final ParallelData<T> parallelData) {
		/* TODO
		// result object
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return;
		}

		if (g.getFilterConfig().hasFiters()) {
			// apply each filter
			for (AbstractFilterFactory<T> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}
		 */
	}

}
