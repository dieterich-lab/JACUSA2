package jacusa.pileup.worker;

import java.util.List;

import jacusa.cli.parameters.RTArrestParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import lib.data.PileupReadInfoData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends PileupReadInfoData>
extends AbstractWorker<T> {

	private final RTArrestParameters<T> rtArrestParameter;
	private final StatisticCalculator<T> statisticCalculator;

	public RTArrestWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId, final List<CopyTmp> copyTmps,
			final RTArrestParameters<T> rtArrestParameter) {

		super(workerDispatcher, threadId, copyTmps, null, rtArrestParameter);
		this.rtArrestParameter = rtArrestParameter;
		statisticCalculator = rtArrestParameter
				.getStatisticParameters().getStatisticCalculator().newInstance();
	}

	@Override
	protected void doWork(final ParallelData<T> parallelData) {
		Result<T> result = new Result<T>();
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
	}

}
