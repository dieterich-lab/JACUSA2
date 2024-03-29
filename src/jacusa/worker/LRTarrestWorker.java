package jacusa.worker;

import jacusa.method.lrtarrest.LRTarrestMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.Result;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.dirmult.DirMultParameter;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.estimation.provider.pileup.RobustEstimationPileupProvider;
import lib.util.Info;
import lib.util.ReplicateContainer;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class LRTarrestWorker
extends AbstractWorker {

	public static final String INFO_VARIANT_SCORE = "call_score";
	
	private ParallelDataValidator validator;
	
	private final AbstractStat stat;
	private final EstimationContainerProvider estContainerProv;
	private final EstimateDirMult dirMult;
	
	public LRTarrestWorker(final LRTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
		
		validator = new ExtendedVariantSiteValidator(method.getTotalBaseCallCountFetcher());
		final MinkaParameter minkaParameter = new MinkaParameter();
		estContainerProv = new RobustEstimationPileupProvider(
				false, minkaParameter.getMaxIterations(), DirMultParameter.ESTIMATED_ERROR);
		dirMult	= new EstimateDirMult(minkaParameter);
	}

	@Override
	protected ParallelData createParallelData(Builder parallelDataBuilder, Coordinate coord) {
		for (int condI = 0; condI < getConditionContainer().getConditionSize() ; ++condI) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(condI);
			for (int replicateI = 0; replicateI < replicateContainer.getReplicateSize() ; ++replicateI) {
				final DataContainer replicate = getConditionContainer().getNullDataContainer(condI, replicateI, coord);
				if (replicate == null) {
					return null;
				}
				parallelDataBuilder.withReplicate(condI, replicateI, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		final Result result = stat.filter(parallelData);
		
		if (validator.isValid(parallelData)) {
			// store variant call result in info field
			final Info resultInfo = result.getResultInfo();
			final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
			final double score = dirMult.getScore(estContainers);
			resultInfo.add(INFO_VARIANT_SCORE, Util.format(score));
		}
		
		return result;
	}
	
}
