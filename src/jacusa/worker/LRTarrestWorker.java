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
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.stat.sample.provider.pileup.RobustEstimationSamplePileupProvider;
import lib.util.Info;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class LRTarrestWorker
extends AbstractWorker {

	public static final String INFO_VARIANT_SCORE = "callStat";
	
	private ParallelDataValidator validator;
	
	private final AbstractStat stat;
	private final EstimationSampleProvider estimationSampleProvider;
	private final EstimateDirMult dirMult;
	
	public LRTarrestWorker(final LRTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
		
		validator = new ExtendedVariantSiteValidator(method.getTotalBaseCallCountFetcher());
		final MinkaParameter minkaParameter = new MinkaParameter();
		estimationSampleProvider = new RobustEstimationSamplePileupProvider(
				false, minkaParameter.getMaxIterations(), DirMultParameter.ESTIMATED_ERROR);
		dirMult	= new EstimateDirMult(minkaParameter);
	}

	@Override
	protected ParallelData createParallelData(Builder parallelDataBuilder, Coordinate coordinate) {
		for (int conditionIndex = 0; conditionIndex < getConditionContainer().getConditionSize() ; ++conditionIndex) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(conditionIndex);
			for (int replicateIndex = 0; replicateIndex < replicateContainer.getReplicateSize() ; ++replicateIndex) {
				final DataContainer replicate = getConditionContainer().getNullDataContainer(conditionIndex, replicateIndex, coordinate);
				if (replicate == null) {
					return null;
				}
				parallelDataBuilder.withReplicate(conditionIndex, replicateIndex, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		final Result result = stat.filter(parallelData);
	
		if (validator.isValid(parallelData)) {
			// store variant call result in info field
			final Info info = result.getResultInfo();
			final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
			final double score = dirMult.getScore(estimationSamples);;
			info.add(INFO_VARIANT_SCORE, Double.toString(score));
		}
		
		return result;
	}
	
}
