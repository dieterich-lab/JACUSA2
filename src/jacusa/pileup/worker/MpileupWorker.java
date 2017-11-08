package jacusa.pileup.worker;

import addvariants.data.WindowedIterator;
import jacusa.cli.parameters.PileupParameters;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.pileup.iterator.variant.AllParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import lib.data.BaseQualData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.util.Coordinate;
import lib.worker.AbstractWorker;

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
			final ParallelData<T> parallelPileup, 
			final WindowedIterator<T> parallelDataIterator) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelPileup);

		/*
		if (getParameters().getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : getParameters().getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}
		*/

		return result;
	}

	/* TODO
	public PileupParameters<T> getParameters() {
		return (PileupParameters<T>) super.getParameters(); 
	}
	*/



	@Override
	protected void doWork() {
		// TODO Auto-generated method stub
		
	}

	
}
