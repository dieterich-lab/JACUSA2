package jacusa.worker;

import jacusa.method.call.CallMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.downsample.SamplePileupCount;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.GenericStat;
import lib.util.Util;
import lib.worker.AbstractWorker;


/**
 * Method "call" specific worker.
 */
public class CallWorker extends AbstractWorker {

	private final AbstractStat stat;
	
	public CallWorker(final CallMethod method, final int threadId) {
		super(method, threadId);
		stat = method
				.getParameter()
				.getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	}

	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.process(parallelData); 
		if (result == null) {
			return null;
		}
		
		// TODO move to stat
		processGenericStats(result);

		// TODO move to stat
		// subsample
		final int subsampleRuns = stat.getSubsampleRuns();
		if (subsampleRuns > 0 ) {
			subsample(subsampleRuns, result);
		}
		// downsample
		final int downsampleRuns = stat.getDownsampleRuns();
		if (downsampleRuns > 0 ) {
			downsample(downsampleRuns, result, stat.getDownsampleFraction());
		}
		// random_sample
		final int randomSampleRuns = stat.getRandomSampleRuns();
		if (randomSampleRuns > 0 ) {
			randomSample(randomSampleRuns, result);
		}
		
		return result;
	}
	
	public int[] pickCondition(final ParallelData parallelData) {
		int pickedConditionIndex = 0;
		int pickedConditionCoverage = parallelData.getPooledData(pickedConditionIndex).getPileupCount().getReads();
		final int conditions = parallelData.getConditions();
	
		for (int conditionIndex = 1; conditionIndex < conditions; conditionIndex++) {
			final int coverage = parallelData.getPooledData(conditionIndex).getPileupCount().getReads();
			if (pickedConditionCoverage < coverage) {
				pickedConditionIndex = conditionIndex;
				pickedConditionCoverage = coverage;
			}
		}
		int otherConditionIndex = -1;
		if (pickedConditionIndex == 1) {
			otherConditionIndex = 0;
		} else {
			otherConditionIndex = 1;
		}
		
		return new int [] {pickedConditionIndex, otherConditionIndex};
	}

	private void subsample(final int subsampleRuns, final Result result ) {
		final ParallelData parallelData = result.getParellelData();

		final int[] conds = pickCondition(parallelData);
		final int picked_cond = conds[0];
		final int other_cond = conds[1];
		
		final Double[] statValues = new Double[subsampleRuns];
		final Double[][] genericStatValues = new Double [genericStats.size()][subsampleRuns];
		
		final int[] targetCoverages = new int[parallelData.getData(other_cond).size()];
		for (int replicateIndex = 0; replicateIndex < parallelData.getData(other_cond).size(); replicateIndex++) {
			targetCoverages[replicateIndex] = parallelData.getData(other_cond).get(replicateIndex).getPileupCount().getReads();
		}
		final PileupCount pileupCount = parallelData.getPooledData(picked_cond).getPileupCount();
		final SamplePileupCount subSampler = new SamplePileupCount(pileupCount);
		final ParallelData template = parallelData.copy();
		
		for (int run = 0; run < subsampleRuns; run++) {
			template.clearCache();
			for (int replicateIndex = 0; replicateIndex < template.getData(other_cond).size(); replicateIndex++) {
				final DataContainer data = template.getDataContainer(other_cond, replicateIndex);
				data.getPileupCount().clear();
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateIndex]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
			}
			
			final Result sampledResult = stat.calculate(template);
			statValues[run] = sampledResult.getScore();

			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				// TODO reuse alpha calculation
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getScore();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().addSite("score_subsampled", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final Double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().addSite(scoreKey + "_subsampled", Util.join(sampledGenericValues, ','));
		}
	}
	
	public int pickSample(final ParallelData parallelData) {
		int pickedSampleI = 0;
		for (int sampleI = 1; sampleI < parallelData.getCombinedData().size(); sampleI++) {
			final int pickedCoverage = parallelData.getCombinedData().get(pickedSampleI).getPileupCount().getReads();
			final int currentCoverage = parallelData.getCombinedData().get(sampleI).getPileupCount().getReads();
			if (currentCoverage < pickedCoverage) {
				pickedSampleI = sampleI;
			}
		}
		
		return pickedSampleI;
	}
	
	private void randomSample(final int randomSampleRuns, final Result result) {
		final ParallelData parallelData = result.getParellelData();

		// container for scores from stats
		final Double[] statValues = new Double[randomSampleRuns];
		final Double[][] genericStatValues = new Double [genericStats.size()][randomSampleRuns];

		// init sampler
		final PileupCount observed = parallelData.getCombPooledData().getPileupCount();
		final SamplePileupCount subSampler = new SamplePileupCount(observed);

		final ParallelData template = parallelData.copy();
		for (int run = 0; run < randomSampleRuns; run++) {
			template.clearCache();
			for (int sampleI = 0; sampleI < template.getCombinedData().size(); sampleI++) {
				final PileupCount sampledPileup = subSampler.sample(parallelData.getCombinedData().get(sampleI).getPileupCount().getReads());
				final PileupCount templatePileup = template.getCombinedData().get(sampleI).getPileupCount();
				templatePileup.clear();
				templatePileup.setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				templatePileup.setINDELCount(sampledPileup.getINDELCount());
			}
			
			final Result sampledResult = stat.calculate(template);
			final double sampledStat = sampledResult.getScore();
			statValues[run] = sampledStat;
			
			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getScore();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().addSite("score_random_sampled", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final Double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().addSite(scoreKey + "_random_sampled", Util.join(sampledGenericValues, ','));
		}
	}
	
	
	private void downsample(final int downsampleRuns, final Result result, final double fraction) {
		final ParallelData parallelData = result.getParellelData();
		
		final int pickedSampleI = pickSample(parallelData);
		final int pickedReads = parallelData.getCombinedData().get(pickedSampleI).getPileupCount().getReads();
		final int targetCoverage = (int)Math.floor(pickedReads * fraction);

		// container for scores from stats
		final Double[] statValues = new Double[downsampleRuns];
		final Double[][] genericStatValues = new Double[genericStats.size()][downsampleRuns];

		// init sampler
		final SamplePileupCount[] subSamplers = new SamplePileupCount[parallelData.getCombinedData().size()];
		for (int sampleI = 0; sampleI < parallelData.getCombinedData().size(); sampleI++) {
			final PileupCount observed = parallelData.getCombinedData().get(sampleI).getPileupCount();
			final SamplePileupCount subSampler = new SamplePileupCount(observed);
			subSamplers[sampleI] = subSampler;
		}
		
		final ParallelData template = parallelData.copy();
		for (int run = 0; run < downsampleRuns; run++) {
			template.clearCache();
			for (int sampleI = 0; sampleI < template.getCombinedData().size(); sampleI++) {
				final SamplePileupCount subSampler = subSamplers[sampleI]; 
				final PileupCount sampledPileup = subSampler.sample(targetCoverage);
				final PileupCount templatePileup = template.getCombinedData().get(sampleI).getPileupCount();
				templatePileup.clear();
				templatePileup.setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				templatePileup.setINDELCount(sampledPileup.getINDELCount());
			}
			
			final Result sampledResult = stat.calculate(template);
			final double sampledStat = sampledResult.getScore();
			statValues[run] = sampledStat;
			
			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getScore();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().addSite("score_downsampled", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final Double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().addSite(scoreKey + "_downsampled", Util.join(sampledGenericValues, ','));
		}
	}
	
}
