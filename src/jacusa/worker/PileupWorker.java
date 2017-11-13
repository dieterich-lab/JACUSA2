package jacusa.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jacusa.cli.parameters.PileupParameters;
import jacusa.data.validator.ParallelDataValidator;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.copytmp.CopyTmpResult;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasPileupCount;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class PileupWorker<T extends AbstractData & hasPileupCount> 
extends AbstractWorker<T> {

	private PileupParameters<T> pileupParameter;
	private CopyTmpResult<T> copyTmpResult;
	private List<CopyTmp> copyTmps;
	
	public PileupWorker(final WorkerDispatcher<T> workerDispatcher, 
			final ParallelDataValidator<T> parallelDataValidator, 
			final PileupParameters<T> pileupParameter) {

		super(workerDispatcher, parallelDataValidator, pileupParameter);
		this.pileupParameter = pileupParameter;
		try {
			copyTmpResult = new CopyTmpResult<T>(getThreadIdContainer().getThreadId(), pileupParameter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		copyTmps = new ArrayList<CopyTmp>(1);
		copyTmps.add(copyTmpResult);
	}

	@Override
	protected void doWork(ParallelData<T> parallelData) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		
		if (pileupParameter.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T, ?> filterFactory : pileupParameter.getFilterConfig().getFilterFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, getConditionContainer());
			}
		}
		
		try {
			copyTmpResult.addResult(result, pileupParameter.getConditionParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}
	
}
