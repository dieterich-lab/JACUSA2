package jacusa.pileup.worker;

import java.util.List;

import jacusa.JACUSA;
import jacusa.cli.parameters.PileupParameters;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.iterator.variant.RTArrestDebugVariantParallelPileup;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.ParallelDataValidator;
import lib.data.BaseQualReadInfoData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.io.copytmp.CopyTmp;
import lib.util.AbstractTool;
import lib.util.Coordinate;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends BaseQualReadInfoData>
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
