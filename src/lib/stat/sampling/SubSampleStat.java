package lib.stat.sampling;

import java.util.List;


import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.downsample.SamplePileupCount;
import lib.data.result.Result;
import lib.stat.INDELstat;
import lib.stat.dirmult.CallStat;
import lib.stat.estimation.ConditionEstimate;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.FastConditionEstimate;
import lib.util.StatUtils;
import lib.util.Util;

public class SubSampleStat {

	private final int runs;
	private final SamplePileupCount subSampler;	
	
	public SubSampleStat(final int runs, final String seed) {
		this.runs = runs;
		subSampler = new SamplePileupCount(seed);
	}

	public int getRuns() {
		return runs;
	}
	
	public void subSample(
			final Result result,
			final CallStat callStat,
			final List<INDELstat> indelStats) {
		final ParallelData parallelData = result.getParellelData();

		// pick condition to sample from
		final int[] conditionIndexes = StatUtils.pickCondition(parallelData);
		final int pickedConditionIndex = conditionIndexes[0];
		final int otherConditionIndex = conditionIndexes[1];
		
		// determine target coverage for the other condition
		final int[] targetCoverages = new int[parallelData.getData(otherConditionIndex).size()];
		for (int replicateIndex = 0; replicateIndex < parallelData.getData(otherConditionIndex).size(); replicateIndex++) {
			targetCoverages[replicateIndex] = parallelData.getData(otherConditionIndex).get(replicateIndex).getPileupCount().getReads();
		}
		
		// Summarise picked pileup - merge replicates
		final PileupCount pileupCount = parallelData.getPooledData(pickedConditionIndex).getPileupCount();
		subSampler.setPileupCount(pileupCount);
		final ParallelData template = parallelData.copy();

		// Container for scores
		final StringBuilder[] scoreSb = new StringBuilder[1 + indelStats.size()]; // call + indels
		// init container for call scores
		scoreSb[0] = new StringBuilder();
		
		// container for estimates
		final ConditionEstimate[] conditionEstimates = new FastConditionEstimate[1 + indelStats.size()]; // call + indels
		// keep estimation of unchanged DirMults of call ...
		conditionEstimates[0] = new FastConditionEstimate(
				callStat
				.getEstimationContainer()
				.getConditionEstimate(pickedConditionIndex));
		// ... and indels
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			conditionEstimates[1 + indelStatIndex] = new FastConditionEstimate(
					indelStats
					.get(indelStatIndex)
					.getEstimationContainer()
					.getConditionEstimate(pickedConditionIndex));
			// Container for scores
			scoreSb[1 + indelStatIndex] = new StringBuilder();
		}
		
		// subsample
		for (int run = 0; run < runs; run++) {
			// clear parallel data
			template.clearCache();
			for (int replicateIndex = 0; replicateIndex < template.getData(otherConditionIndex).size(); replicateIndex++) {
				final DataContainer data = template.getDataContainer(otherConditionIndex, replicateIndex);
				data.getPileupCount().clear();
				
				// sample pileup ...
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateIndex]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
				// TODO modification count
			}
			
			final EstimationContainer callEstimationContainer = callStat.getConditionEstimateProvider().convert(template);
			// inject previous estimation for unchanged condition
			callEstimationContainer.updateCondition(pickedConditionIndex, conditionEstimates[0], otherConditionIndex);
			// estimate alpha values
			String callEstimationResult = "1";
			if (! callStat.getMinka().estimate(callEstimationContainer)) {
				callEstimationResult = "0";
			}
			
			if (run > 0) {
				scoreSb[0].append(',');
				result.getResultInfo().append("score_subsampled_estimation", ",");
				for (final ConditionEstimate conditionEstimate : callEstimationContainer.getEstimates()) {
					result.getResultInfo().append("score_subsampled_backtrack" + conditionEstimate.getID(), "|");
					result.getResultInfo().append("score_subsampled_reset" + conditionEstimate.getID(), "|");
					// TODO show alphas
				}
			}
			result.getResultInfo().add("subsampled_score_estimation", callEstimationResult);
			for (final ConditionEstimate conditionEstimate : callEstimationContainer.getEstimates()) {
				result.getResultInfo().append(
						"score_subsampled_backtrack" + conditionEstimate.getID(),
						Util.join(conditionEstimate.getBacktracks(), ','));
				result.getResultInfo().append(
						"score_subsampled_reset" + conditionEstimate.getID(),
						Util.join(conditionEstimate.getResets(), ','));
				// TODO show alphas
			}
			// add subsampled scores
			final double callScore = callStat.getStat(callEstimationContainer);
			scoreSb[0].append(callScore);

			for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
				final INDELstat indelStat = indelStats.get(indelStatIndex);
				final EstimationContainer indelEstimationContainer = indelStat.getEstimationContainerProvider().convert(template);
				// inject previous estimation for unchanged condition
				indelEstimationContainer.updateCondition(pickedConditionIndex, conditionEstimates[1 + indelStatIndex], otherConditionIndex);
				// estimate alpha values
				String indelEstimationResult = "1";
				if (! indelStat.getMinka().estimate(indelEstimationContainer)) {
					indelEstimationResult = "0";
				}
				
				if (run > 0) {
					scoreSb[1 + indelStatIndex].append(',');
					result.getResultInfo().append(indelStat.getScoreKey() + "_subsampled_estimation", ",");
					for (final ConditionEstimate conditionEstimate : indelEstimationContainer.getEstimates()) {
						result.getResultInfo().append(indelStat.getScoreKey() + "_score_subsampled_backtrack" + conditionEstimate.getID(), "|");
						result.getResultInfo().append(indelStat.getScoreKey() + "_score_subsampled_reset" + conditionEstimate.getID(), "|");
						// TODO show alphas
					}
				}
				result.getResultInfo().add(indelStat.getScoreKey() +"_score_estimation", indelEstimationResult);
				for (final ConditionEstimate conditionEstimate : indelEstimationContainer.getEstimates()) {
					result.getResultInfo().append(
							indelStat.getScoreKey() + "_score_subsampled_backtrack" + conditionEstimate.getID(),
							Util.join(conditionEstimate.getBacktracks(), ','));
					result.getResultInfo().append(
							indelStat.getScoreKey() + "_score_subsampled_reset" + conditionEstimate.getID(),
							Util.join(conditionEstimate.getResets(), ','));
					// TODO show alphas
				}
				// add subsampled scores
				final double indelScore = indelStat.getMinka().getLRT(indelEstimationContainer);
				scoreSb[1 + indelStatIndex].append(indelScore);
			}
		}

		// write scores to Info
		result.getResultInfo().add("score_subsampled", scoreSb[0].toString());
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			final INDELstat indelStat = indelStats.get(indelStatIndex);
			result.getResultInfo().add(
					indelStat.getScoreKey() + "_subsampled",
					scoreSb[1 + indelStatIndex].toString());
		}
	}
	
}
