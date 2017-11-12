package jacusa.pileup.worker;

import java.util.List;

import jacusa.cli.parameters.PileupParameters;

import lib.data.ParallelData;
import lib.data.basecall.PileupData;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class MpileupWorker<T extends PileupData> 
extends AbstractWorker<T> {

	public MpileupWorker(final WorkerDispatcher<T> workerDispatcher,
			final List<CopyTmp> copyTmps, final PileupParameters<T> parameter) {

		super(workerDispatcher, copyTmps, null, parameter);
	}

	@Override
	protected void doWork(ParallelData<T> parallelData) {
		// TODO
		/*
		Result<T> result = new Result<T>();
		result.setParallelData(parallelPileup);
		
		if (getParameters().getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : getParameters().getFilterConfig().getFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, parallelDataIterator);
			}
		}
		*/
	}

}
