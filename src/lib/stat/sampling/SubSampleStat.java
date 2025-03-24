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
import lib.stat.estimation.FastConditionEstimate;
import lib.util.StatUtils;

public class SubSampleStat {

	private final int runs;
	
	public SubSampleStat(final int runs) {
		this.runs = runs;
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
		
		// coverages
		final int[] targetCoverages = new int[parallelData.getData(otherConditionIndex).size()];
		for (int replicateIndex = 0; replicateIndex < parallelData.getData(otherConditionIndex).size(); replicateIndex++) {
			targetCoverages[replicateIndex] = parallelData.getData(otherConditionIndex).get(replicateIndex).getPileupCount().getReads();
		}
		// picked PileupCount
		final PileupCount pileupCount = parallelData.getPooledData(pickedConditionIndex).getPileupCount();
		final SamplePileupCount subSampler = new SamplePileupCount(pileupCount);
		final ParallelData template = parallelData.copy();

		// StringBuilder to add scores
		final StringBuilder callScoresSb = new StringBuilder();
		final StringBuilder[] indelScoreSbs = new StringBuilder[indelStats.size()];

		// keep estimation of unchanged DirMults
		final ConditionEstimate callPickedConditionEstimate = new FastConditionEstimate(
				callStat.getEstimationContainer().getConditionEstimate(pickedConditionIndex));
		final ConditionEstimate[] indelPickedConditionEstimate = new ConditionEstimate[indelStats.size()];
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			indelPickedConditionEstimate[indelStatIndex] = new FastConditionEstimate(
					indelStats.get(otherConditionIndex).getEstimationContainer().getConditionEstimate(pickedConditionIndex));
		}
			
		for (int run = 0; run < runs; run++) {
			template.clearCache();
			for (int replicateIndex = 0; replicateIndex < template.getData(otherConditionIndex).size(); replicateIndex++) {
				final DataContainer data = template.getDataContainer(otherConditionIndex, replicateIndex);
				data.getPileupCount().clear();
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateIndex]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
			}
			
			// inject previous estimation for unchanged condition
			callStat.getEstimationContainer().updateCondition(otherConditionIndex, callPickedConditionEstimate);
			callScoresSb.append(callStat.getStat(callStat.getEstimationContainer()));
			
			for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
				final INDELstat indelStat = indelStats.get(indelStatIndex);
				indelStat.getEstimationContainer().updateCondition(otherConditionIndex, indelPickedConditionEstimate[indelStatIndex]);
				callScoresSb.append(indelStat.getLRT(indelStat.getEstimationContainer()));
				
				final StringBuilder indelScoreSb = indelScoreSbs[indelStatIndex];
				final double statValue = indelStat.getLRT(callStat.getEstimationContainer()); 
				indelScoreSb.append(',');
				indelScoreSb.append(statValue);
			}
		}

		result.getResultInfo().add("score_subsampled", callScoresSb.toString());
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			final INDELstat indelStat = indelStats.get(indelStatIndex);
			final StringBuilder indelScoreSb = indelScoreSbs[indelStatIndex];
			result.getResultInfo().add(indelStat.getScoreKey() + "_subsampled", indelScoreSb.toString());
		}
	}

	
}
