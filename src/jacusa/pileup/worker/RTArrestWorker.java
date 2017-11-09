package jacusa.pileup.worker;

import jacusa.JACUSA;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.iterator.variant.RTArrestDebugVariantParallelPileup;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import lib.data.BaseQualReadInfoData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.util.AbstractTool;
import lib.util.Coordinate;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class RTArrestWorker<T extends BaseQualReadInfoData>
extends AbstractWorker<T> {

	private RTArrestParameters<T> parameters;
	private StatisticCalculator<T> statisticCalculator;
	
	private final Variant<T> variant;
	
	public RTArrestWorker(
			final WorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final RTArrestParameters<T> parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);
		this.parameters = parameters;
		statisticCalculator = parameters.getStatisticParameters()
				.getStatisticCalculator().newInstance();

		if (parameters.isDebug()) {
			AbstractTool.getLogger().addDebug("Overwrite file format -> RTArrestDebugVariantParallelPileup");
			variant = new RTArrestDebugVariantParallelPileup<T>();
		} else {
			variant = new RTArrestVariantParallelPileup<T>();
		}
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
			for (AbstractFilterFactory<T> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}

		return result;
	}

}
