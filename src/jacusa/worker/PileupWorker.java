package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.PileupParameter;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasPileupCount;
import lib.data.result.DefaultResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class PileupWorker<T extends AbstractData & hasPileupCount> 
extends AbstractWorker<T, DefaultResult<T>> {
	
	public PileupWorker(final WorkerDispatcher<T, DefaultResult<T>> workerDispatcher, 
			final int threadId,
			final CopyTmpResult<T, DefaultResult<T>> copyTmpResult,
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final PileupParameter<T> pileupParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, pileupParameter);
	}

	@Override
	protected DefaultResult<T> process(final ParallelData<T> parallelData) {
		return new DefaultResult<T>(parallelData);
	}	
}
