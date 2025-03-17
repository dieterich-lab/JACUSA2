package lib.stat.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.downsample.SamplePileupCount;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.StatUtils;
import lib.util.Util;

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

public class SubSampleStat {

	private final int runs;
	private final Map<String, List<Double>> statName2scores;
	
	public SubSampleStat(final int runs, final Set<String> statNames, final List<Integer> replicateIntegers) {
		this.runs = runs;
		statName2scores = new HashMap<String, List<Double>>(statNames.size());
		for (final String statName : statNames) {
			statName2scores.put(statName, new ArrayList<Double>(Util.noRehashCapacity(runs)));
		}
		
	}

	public int getRuns() {
		return runs;
	}
	
	public void subSample(Result result, Map<String, AbstractStat> name2stat) {
		// TODO check statNames match 
		final ParallelData parallelData = result.getParellelData();

		final int[] conditionIndexes = StatUtils.pickCondition(parallelData);
		final int pickedConditionIndex = conditionIndexes[0];
		final int otherConditionIndex = conditionIndexes[1];
		
		final int[] targetCoverages = new int[parallelData.getData(otherConditionIndex).size()];
		for (int replicateIndex = 0; replicateIndex < parallelData.getData(otherConditionIndex).size(); replicateIndex++) {
			targetCoverages[replicateIndex] = parallelData.getData(otherConditionIndex).get(replicateIndex).getPileupCount().getReads();
		}
		final PileupCount pileupCount = parallelData.getPooledData(pickedConditionIndex).getPileupCount();
		final SamplePileupCount subSampler = new SamplePileupCount(pileupCount);
		final ParallelData template = parallelData.copy();
		
		for (int run = 0; run < runs; run++) {
			template.clearCache();
			for (int replicateIndex = 0; replicateIndex < template.getData(otherConditionIndex).size(); replicateIndex++) {
				final DataContainer data = template.getDataContainer(otherConditionIndex, replicateIndex);
				data.getPileupCount().clear();
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateIndex]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
			}
			
			for (final Entry<String, AbstractStat> entry : name2stat.entrySet()) {
				// TODO reuse alpha calculation
				final Result sampledStatResult 	= entry.getValue().calculate(template);
				final double sampledStat 		= sampledStatResult.getScore();
				statName2scores.get(entry.getKey()).add(sampledStat);
			}
		}

		// write successful sampling
		for (final String statName : name2stat.keySet()) {
			result.getResultInfo().addSite(statName + "_subsampled", Util.join(statName2scores.get(statName), ','));
		}
	}

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
	
	
}
