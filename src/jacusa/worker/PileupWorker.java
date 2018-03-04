package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.PileupParameter;

import lib.data.ParallelData;
import lib.data.PileupData;
import lib.data.result.DefaultResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class PileupWorker
extends AbstractWorker<PileupData, DefaultResult<PileupData>> {
	
	public PileupWorker(final WorkerDispatcher<PileupData, DefaultResult<PileupData>> workerDispatcher, 
			final int threadId,
			final CopyTmpResult<PileupData, DefaultResult<PileupData>> copyTmpResult,
			final List<ParallelDataValidator<PileupData>> parallelDataValidators, 
			final PileupParameter pileupParameter) {

		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, pileupParameter);
	}

	@Override
	protected DefaultResult<PileupData> process(final ParallelData<PileupData> parallelData) {
		return new DefaultResult<PileupData>(parallelData);
	}	
}
