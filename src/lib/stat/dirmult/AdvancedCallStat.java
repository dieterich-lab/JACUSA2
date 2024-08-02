package lib.stat.dirmult;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.Util;

public class AdvancedCallStat extends AbstractStat {

	private CallStat stat;
	private int runs;
	private int limit;

	public AdvancedCallStat(final CallStat stat, final int runs, final int limit) {
		this.stat = stat;
		this.runs = runs;
		this.limit = limit;
	}

	@Override
	protected boolean filter(Result statResult) {
		return stat.filter(statResult);
	}

	@Override
	public Result calculate(ParallelData parallelData) {
		final Result result = this.stat.calculate(parallelData);
		// pick condition
		int picked_cond = -1;
		int picked_cond_cov = -1;
		final int conditions = parallelData.getConditions();

		for (int condI = 0; condI < conditions - 1; condI++) {
			final int coverage = parallelData.getPooledData(condI).getCoverage().getValue();
			if (picked_cond < 0) {
				picked_cond = condI;
				picked_cond_cov = coverage;
			} else if (picked_cond_cov < coverage) {
				picked_cond = condI;
				picked_cond_cov = coverage;
			}
		}
		// TODO make nicer for conditions > 2
		int other_cond = -1;
		if (picked_cond == 1) {
			other_cond = 0;
		} else {
			other_cond = 1;
		}

		/*
		for (int condI = 0; condI < 2; condI++) {
			System.out.println("observed: " + condI);
			System.out.println(parallelData.getPooledData(condI).getPileupCount().getBCC().toString());
		}
		*/

		// sample
		final double[] values = new double[this.runs];
		int check = 0;
		for (int run = 0; run < this.runs; run++) {
			final DataContainer pooledData = parallelData.getPooledData(picked_cond).copy();
			final int[] targetCoverages = new int[parallelData.getData(other_cond).size()];
			for (int replicateI = 0; replicateI < parallelData.getData(other_cond).size(); replicateI++) {
				targetCoverages[replicateI] = parallelData.getData(other_cond).get(replicateI).getCoverage().getValue();
			}

			Builder builder = new Builder(
					parallelData.getConditions(),
					parallelData.getReplicates());
			for (int replicateI = 0; replicateI < parallelData.getData(picked_cond).size(); replicateI++) {
				builder.withReplicate(picked_cond, replicateI, parallelData.getData(picked_cond).get(replicateI));
			}
			// TODO make this efficient
			for (int replicateI = 0; replicateI < parallelData.getData(other_cond).size(); replicateI++) {
				final DataContainer data = parallelData.getData(other_cond).get(replicateI).copy();
				data.getPileupCount().substract(data.getPileupCount());
				builder.withReplicate(other_cond, replicateI, data);
				
			}

			final ParallelData sampledData = builder
					.sample(other_cond, pooledData, targetCoverages)
					.build();

			/*
			for (int condI = 0; condI < 2; condI++) {
				System.out.println("sampled " + run + ": " + condI);
				System.out.println(sampledData.getPooledData(condI).getPileupCount().getBCC().toString());
			}
			*/

			final Result sampledResult = this.stat.calculate(sampledData);
			final double sampledStat = sampledResult.getStat();
			values[run] = sampledStat;
			if ((result.getStat() - sampledStat) > 0) {
				check++;
			}
		}
		// write successful sampling
		result.getResultInfo().add("subsampling_scores", Util.join(values, ','));
		if (check >= limit) {
			result.getResultInfo().add("subsampling", "passed");
		} else {
			result.getResultInfo().add("subsampling", "failed");
		}

		return result;
	}

	@Override
	protected void addStatResultInfo(Result statResult) {
		stat.addStatResultInfo(statResult);
	}

}
