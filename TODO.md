[x] adjust core data types
[x] adjust worker
[x] fix TODOs, FIXMEs
[x] test against existing runs
------------------------------
[x] adjust QutRNA to extended output
[x] fix TODOs, FIXMEs
[x] rerun & test
[x] custom contrasts, take arbitrary subSamples -> expanded sample_table
[ ] -I pseudoCount=, ditMult etc.
 
 
[ ] add the following:

/* TODO implement other methods
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
*/


	/* TODO implement
	 * 	
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
	 */
	
	/* TODO implement
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
	*/