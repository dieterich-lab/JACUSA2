package jacusa.worker;

import java.util.List;

import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.LRTarrestData;
import lib.data.ParallelData;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.result.StatisticResult;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class LRTarrestWorker
extends AbstractWorker<LRTarrestData, StatisticResult<LRTarrestData>> {

	private final double threshold;
	private final AbstractStatisticCalculator<LRTarrestData> statisticCalculator;
	
	public LRTarrestWorker(
			final ReferenceSetter<LRTarrestData> referenceSetter,
			final WorkerDispatcher<LRTarrestData, StatisticResult<LRTarrestData>> workerDispatcher,
			final int threadId,
			final CopyTmpResult<LRTarrestData, StatisticResult<LRTarrestData>> copyTmpResult,
			final List<ParallelDataValidator<LRTarrestData>> parallelDataValidators, 
			final LRTarrestParameter lrtArrestParameter) {

		super(referenceSetter, workerDispatcher, threadId, copyTmpResult, parallelDataValidators, lrtArrestParameter);
		threshold = lrtArrestParameter.getStatisticParameters().getThreshold();
		statisticCalculator = lrtArrestParameter
				.getStatisticParameters().newInstance();
	}

	@Override
	protected StatisticResult<LRTarrestData> process(final ParallelData<LRTarrestData> parallelData) {
		return statisticCalculator.filter(threshold, parallelData);
	}
	
}
