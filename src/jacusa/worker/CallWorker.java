package jacusa.worker;

import jacusa.method.call.CallMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.downsample.SamplePileupCount;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.GenericStat;
import lib.util.Base;
import lib.util.Util;
import lib.worker.AbstractWorker;


/**
 * Method "call" specific worker.
 */
public class CallWorker extends AbstractWorker {

	private final AbstractStat stat;
	private final boolean addExtra;
	
	public CallWorker(final CallMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
		
		addExtra = true;
	}

	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.filter(parallelData); 
		if (result == null) {
			return null;
		}
		processGenericStats(result);
		
		/* TODO remove not needed
		final SortedSet<BaseSub> baseSubs = getParameter().getReadTags();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubResult(baseSubs, DataType.BASE_SUBST2BCC.getFetcher(), result);
		}
		*/

		if (addExtra) {
			final int[] replicates = parallelData.getReplicates().stream().mapToInt(Integer::intValue).toArray();
			
			int[] reads = new int[parallelData.getCombinedData().size()]; 
			int[] insCount = new int[parallelData.getCombinedData().size()];
			int[] delCount = new int[parallelData.getCombinedData().size()];
			
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < reads.length; ++i) {
				final PileupCount pileupCount = parallelData.getCombinedData().get(i).getPileupCount();
				reads[i] = pileupCount.getReads();

				insCount[i] = pileupCount.getINDELCount().getInsertionCount();
				delCount[i] = pileupCount.getINDELCount().getDeletionCount();

				if (i != 0) {
					sb.append('|');
				}
				final BaseCallQualityCount bcqc = pileupCount.getBaseCallQualityCount();
				boolean check1 = false;
				boolean check2 = false;
				for (final Base base: Base.validValues()) {
					if (check1) {
						sb.append(',');
					}
					for (final Byte qual : bcqc.getBaseCallQuality(base)) {
						if (check2) {
							sb.append('&');
						}
						sb.append(qual);
						sb.append(':');
						sb.append(bcqc.getBaseCallQuality(base, qual));
						check2 = true;
					}
					check1 = true;
				}
			}
			result.getResultInfo().add("reads", Util.pack(reads, replicates, ',', ','));
			if (getParameter().showInsertionCount() || getParameter().showInsertionStartCount()) {
				result.getResultInfo().add("insertions", Util.pack(insCount, replicates, ',', ','));
			}
			if (getParameter().showDeletionCount()) {
				result.getResultInfo().add("deletions", Util.pack(delCount, replicates, ',', ','));
			}
			
			result.getResultInfo().add("bcqs", sb.toString());
		}

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
		int picked_cond = 0;
		int picked_cond_cov = parallelData.getPooledData(picked_cond).getPileupCount().getReads();
		final int conditions = parallelData.getConditions();
	
		for (int condI = 1; condI < conditions; condI++) {
			final int coverage = parallelData.getPooledData(condI).getPileupCount().getReads();
			if (picked_cond_cov < coverage) {
				picked_cond = condI;
				picked_cond_cov = coverage;
			}
		}
		int other_cond = -1;
		if (picked_cond == 1) {
			other_cond = 0;
		} else {
			other_cond = 1;
		}
		
		return new int [] {picked_cond, other_cond};
	}

	private void subsample(final int subsampleRuns, final Result result ) {
		final ParallelData parallelData = result.getParellelData();

		final int[] conds = pickCondition(parallelData);
		final int picked_cond = conds[0];
		final int other_cond = conds[1];
		
		final double[] statValues = new double[subsampleRuns];
		final double[][] genericStatValues = new double [genericStats.size()][subsampleRuns];
		
		final int[] targetCoverages = new int[parallelData.getData(other_cond).size()];
		for (int replicateI = 0; replicateI < parallelData.getData(other_cond).size(); replicateI++) {
			targetCoverages[replicateI] = parallelData.getData(other_cond).get(replicateI).getPileupCount().getReads();
		}
		final PileupCount pileupCount = parallelData.getPooledData(picked_cond).getPileupCount();
		final SamplePileupCount subSampler = new SamplePileupCount(pileupCount);
		final ParallelData template = parallelData.copy();
		
		for (int run = 0; run < subsampleRuns; run++) {
			template.clearCache();
			for (int replicateI = 0; replicateI < template.getData(other_cond).size(); replicateI++) {
				final DataContainer data = template.getDataContainer(other_cond, replicateI);
				data.getPileupCount().clear();
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateI]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
			}
			
			final Result sampledResult = stat.calculate(template);
			statValues[run] = sampledResult.getStat();

			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				// TODO reuse alpha calculation
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getStat();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().add("score_subsampled", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().add(scoreKey + "_subsampled", Util.join(sampledGenericValues, ','));
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
		final double[] statValues = new double[randomSampleRuns];
		final double[][] genericStatValues = new double [genericStats.size()][randomSampleRuns];

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
			final double sampledStat = sampledResult.getStat();
			statValues[run] = sampledStat;
			
			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getStat();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().add("score_random_sample", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().add(scoreKey + "_random_sample", Util.join(sampledGenericValues, ','));
		}
	}
	
	
	private void downsample(final int downsampleRuns, final Result result, final double fraction) {
		final ParallelData parallelData = result.getParellelData();
		
		final int pickedSampleI = pickSample(parallelData);
		final int pickedReads = parallelData.getCombinedData().get(pickedSampleI).getPileupCount().getReads();
		final int targetCoverage = (int)Math.floor(pickedReads * fraction);

		// container for scores from stats
		final double[] statValues = new double[downsampleRuns];
		final double[][] genericStatValues = new double [genericStats.size()][downsampleRuns];

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
			final double sampledStat = sampledResult.getStat();
			statValues[run] = sampledStat;
			
			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				
				final Result sampledGenericStatResult = genericStat.calculate(template);
				final double sampledGenericStat = sampledGenericStatResult.getStat();
				
				genericStatValues[genericStatI][run] = sampledGenericStat;				
			}
		}
		// write successful sampling
		result.getResultInfo().add("score_downsampled", Util.join(statValues, ','));

		// write successful sampling
		for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
			final GenericStat genericStat = genericStats.get(genericStatI);
			final String scoreKey = genericStat.getScoreKey();

			final double[] sampledGenericValues = genericStatValues[genericStatI];
			result.getResultInfo().add(scoreKey + "_downsampled", Util.join(sampledGenericValues, ','));
		}
	}
	
}
