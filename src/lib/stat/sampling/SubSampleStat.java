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
import lib.util.ExtendedInfo;
// import lib.util.ExtendedInfo;
import lib.util.StatUtils;

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
		
		// coverages
		final int[] targetCoverages = new int[parallelData.getData(otherConditionIndex).size()];
		for (int replicateIndex = 0; replicateIndex < parallelData.getData(otherConditionIndex).size(); replicateIndex++) {
			targetCoverages[replicateIndex] = parallelData.getData(otherConditionIndex).get(replicateIndex).getPileupCount().getReads();
		}
		// picked PileupCount
		final PileupCount pileupCount = parallelData.getPooledData(pickedConditionIndex).getPileupCount();
		subSampler.setPileupCount(pileupCount);
		final ParallelData template = parallelData.copy();

		// StringBuilder to add scores
		final StringBuilder callScoresSb = new StringBuilder();
		final StringBuilder[] indelScoreSbs = new StringBuilder[indelStats.size()];

		// keep estimation of unchanged DirMults
		/*final ConditionEstimate callPickedConditionEstimate = new FastConditionEstimate(
				callStat.getEstimationContainer().getConditionEstimate(pickedConditionIndex));
				*/
		final ConditionEstimate[] indelPickedConditionEstimate = new ConditionEstimate[indelStats.size()];
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			indelPickedConditionEstimate[indelStatIndex] = new FastConditionEstimate(
					indelStats.get(otherConditionIndex).getEstimationContainer().getConditionEstimate(pickedConditionIndex));
			indelScoreSbs[indelStatIndex] = new StringBuilder();
		}
			
		StringBuilder TMP = new StringBuilder();
		for (int run = 0; run < runs; run++) {
			template.clearCache();
			for (int replicateIndex = 0; replicateIndex < template.getData(otherConditionIndex).size(); replicateIndex++) {
				final DataContainer data = template.getDataContainer(otherConditionIndex, replicateIndex);
				data.getPileupCount().clear();
				final PileupCount sampledPileup = subSampler.sample(targetCoverages[replicateIndex]);
				data.getPileupCount().setBaseCallQualityCount(sampledPileup.getBaseCallQualityCount());
				data.getPileupCount().setINDELCount(sampledPileup.getINDELCount());
				// TODO modification count
				TMP.append("__" + data.getPileupCount().toString().replace('\n', '_'));
			}
			
			/*
			// inject previous estimation for unchanged condition
			callStat.getEstimationContainer().updateCondition(pickedConditionIndex, callPickedConditionEstimate, otherConditionIndex);
			callStat.getMinka().estimate(callStat.getEstimationContainer(), new ExtendedInfo()); // FIXME
			callScoresSb.append(callStat.getStat(callStat.getEstimationContainer()));
			*/
			if (run > 0) {
				callScoresSb.append(',');
			}
			Result score = callStat.process(template, new ExtendedInfo());
			callScoresSb.append(score.getScore());
			System.out.println(score.getScore());
			System.out.println(template.getData(0).get(0).getPileupCount().toString());
			System.out.println(template.getData(1).get(0).getPileupCount().toString());
			
			for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
				final INDELstat indelStat = indelStats.get(indelStatIndex);
				/*
				indelStat.getEstimationContainer().updateCondition(pickedConditionIndex, indelPickedConditionEstimate[indelStatIndex], otherConditionIndex);
				indelStat.getMinka().estimate(callStat.getEstimationContainer(), new ExtendedInfo()); // FIXME
				callScoresSb.append(indelStat.getLRT(indelStat.getEstimationContainer()));
				*/
				
				final StringBuilder indelScoreSb = indelScoreSbs[indelStatIndex];
				final Result indel = indelStat.process(template, new ExtendedInfo()); 
				if (run > 0) {
					indelScoreSb.append(',');
				}
				indelScoreSb.append(indel.getScore());
			}
		}
		result.getResultInfo().add("debug", TMP.toString());

		result.getResultInfo().add("score_subsampled", callScoresSb.toString());
		for (int indelStatIndex = 0; indelStatIndex < indelStats.size(); indelStatIndex++) {
			final INDELstat indelStat = indelStats.get(indelStatIndex);
			final StringBuilder indelScoreSb = indelScoreSbs[indelStatIndex];
			result.getResultInfo().add(indelStat.getScoreKey() + "_subsampled", indelScoreSb.toString());
		}
	}

	
}
