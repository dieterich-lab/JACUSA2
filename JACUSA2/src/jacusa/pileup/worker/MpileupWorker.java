package jacusa.pileup.worker;

import jacusa.cli.parameters.PileupParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.pileup.iterator.WindowedIterator;
import jacusa.pileup.iterator.variant.AllParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;

public class MpileupWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	private final Variant<T> variant;
	
	public MpileupWorker(MpileupWorkerDispatcher<T> workerDispatcher,
			int threadId,
			PileupParameters<T> parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);
		variant = new AllParallelPileup<T>();
	}

	@Override
	protected Result<T> processParallelData(
			final ParallelPileupData<T> parallelPileup, 
			final WindowedIterator<T> parallelDataIterator) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelPileup);

		if (getParameters().getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : getParameters().getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}

		return result;
	}

	@Override
	protected WindowedIterator<T> buildIterator(final Coordinate coordinate) {
		return new WindowedIterator<T>(coordinate, variant, getReaders(), getParameters());
	}

	public PileupParameters<T> getParameters() {
		return (PileupParameters<T>) super.getParameters(); 
	}
	
}
