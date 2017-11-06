package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.dispatcher.call.CallWorkerDispatcher;
import jacusa.pileup.iterator.WindowedIterator;
import jacusa.pileup.iterator.variant.OneConditionVariantParallelPileup;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;

public class CallWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	final private CallParameters<T> parameters;
	
	final private StatisticCalculator<T> statisticCalculator;
	
	public CallWorker(
			final CallWorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final CallParameters<T> parameters) {
		super(workerDispatcher,
				threadId,
				parameters);

		this.statisticCalculator = parameters.getStatisticParameters().getStatisticCalculator();
		
		this.parameters = parameters;
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
			for (final AbstractFilterFactory<T> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}

		return result;
	}
	
	// return new OneConditionCallIterator<T>(coordinate, variant, getReaders(), getParameters());
	
	@Override
	protected WindowedIterator<T> buildIterator(final Coordinate coordinate) {
		if (getParameters().getConditions() == 1) {
			return new WindowedIterator<T>(coordinate, new OneConditionVariantParallelPileup<T>(), getReaders(), parameters);
		}
		
		return new WindowedIterator<T>(coordinate, new VariantParallelPileup<T>(), getReaders(), parameters);
	}

}
