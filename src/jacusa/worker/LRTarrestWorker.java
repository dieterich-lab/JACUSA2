package jacusa.worker;

import jacusa.method.lrtarrest.LRTarrestMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.dirmult.DirMultParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.ConditionEstimateProvider;
import lib.stat.estimation.provider.pileup.RobustEstimationPileupProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;
import lib.worker.AbstractWorker;

public class LRTarrestWorker
extends AbstractWorker {

	public static final String INFO_VARIANT_SCORE = "call_score";
	
	private ParallelDataValidator validator;
	
	private final AbstractStat stat;
	private final ConditionEstimateProvider estimationContainerProvider;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;
	
	public LRTarrestWorker(final LRTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method
				.getParameter()
				.getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
		
		validator = new ExtendedVariantSiteValidator(method.getTotalBaseCallCountFetcher());
		final MinkaParameter minkaParameter = new MinkaParameter();
		estimationContainerProvider = new RobustEstimationPileupProvider(
				false, minkaParameter.getMaxIterations(), DirMultParameter.ESTIMATED_ERROR);
		estimateDirMultAlpha = new MinkaEstimateDirMultAlpha(minkaParameter);
	}

	@Override
	protected Result process(final ParallelData parallelData) {
		final Result result = stat.process(parallelData, null);
		
		if (validator.isValid(parallelData)) {
			// store variant call result in info field
			final ExtendedInfo resultInfo = result.getResultInfo();
			final EstimationContainer estimationContainer = estimationContainerProvider.convert(parallelData);
			final double score = estimateDirMultAlpha.getScore(estimationContainer);
			resultInfo.addSite(INFO_VARIANT_SCORE, Util.format(score));
		}
		
		return result;
	}
	
}
