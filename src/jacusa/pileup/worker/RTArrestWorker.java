package jacusa.pileup.worker;

import jacusa.JACUSA;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.data.BaseQualReadInfoData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;
import jacusa.pileup.iterator.WindowedIterator;
import jacusa.pileup.iterator.variant.RTArrestDebugVariantParallelPileup;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;

public class RTArrestWorker<T extends BaseQualReadInfoData>
extends AbstractWorker<T> {

	private RTArrestParameters<T> parameters;
	private StatisticCalculator<T> statisticCalculator;
	
	private final Variant<T> variant;
	
	public RTArrestWorker(
			final RTArrestWorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final RTArrestParameters<T> parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);
		this.parameters = parameters;
		statisticCalculator = parameters.getStatisticParameters()
				.getStatisticCalculator().newInstance();

		if (parameters.isDebug()) {
			JACUSA.printDebug("Overwrite file format -> RTArrestDebugVariantParallelPileup");
			variant = new RTArrestDebugVariantParallelPileup<T>();
		} else {
			variant = new RTArrestVariantParallelPileup<T>();
		}
	}

	@Override
	protected WindowedIterator<T> buildIterator(final Coordinate coordinate) {
		return new WindowedIterator<T>(coordinate, variant, getReaders(), parameters);
	}
	
	@Override
	protected Result<T> processParallelData(final ParallelPileupData<T> parallelData, 
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
