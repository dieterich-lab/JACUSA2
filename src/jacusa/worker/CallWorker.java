package jacusa.worker;

import java.util.Arrays;

import jacusa.method.call.CallMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
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

		/* TODO remove not needed
		final SortedSet<BaseSub> baseSubs = getParameter().getReadTags();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubResult(baseSubs, DataType.BASE_SUBST2BCC.getFetcher(), result);
		}
		*/

		if (addExtra) {
			final Base refBase = parallelData.getCombPooledData().getAutoRefBase();
			final int[] replicates = parallelData.getReplicates().stream().mapToInt(Integer::intValue).toArray();
			
			int[] reads = new int[parallelData.getCombinedData().size()]; 
			int[] insCount = new int[parallelData.getCombinedData().size()];
			int[] delCount = new int[parallelData.getCombinedData().size()];
			
			double[] insRatio = new double[parallelData.getCombinedData().size()];
			double[] delRatio = new double[parallelData.getCombinedData().size()];

			double[] nonRefRatio = new double[parallelData.getCombinedData().size()];
			String[] baseRatio = new String[parallelData.getCombinedData().size()];
			
			for (int i = 0; i < insRatio.length; ++i) {
				final PileupCount pileup = parallelData.getCombinedData().get(i).getPileupCount();
				reads[i] = pileup.getReads();

				insCount = new int[parallelData.getCombinedData().size()];
				insRatio[i] = pileup.getINDELCount().getInsertionRatio(reads[i]);

				delCount = new int[parallelData.getCombinedData().size()];
				delRatio[i] = pileup.getINDELCount().getDeletionRatio(reads[i]);
			
				nonRefRatio[i] = pileup.getBCC().getNonRefRatio(refBase);
				baseRatio[i] = Util.join(pileup.getBCC().getRatio(Base.validValues()), ',');
			}
			result.getResultInfo().add("reads", Util.pack(reads, replicates, ',', ';'));
			if (getParameter().showInsertionCount() || getParameter().showInsertionStartCount()) {
				result.getResultInfo().add("ins", Util.pack(insCount, replicates, ',', ';'));
				result.getResultInfo().add("ins_ratio", Util.pack(insRatio, replicates, ',', ';'));
			}
			if (getParameter().showDeletionCount()) {
				result.getResultInfo().add("del", Util.pack(delCount, replicates, ',', ';'));
				result.getResultInfo().add("del_ratio", Util.pack(delRatio, replicates, ',', ';'));
			}
			
			result.getResultInfo().add("non_ref_ratio", Util.pack(nonRefRatio, replicates, ',', ';'));
			result.getResultInfo().add("base_ratio", Util.pack(baseRatio, replicates, '|', ';'));
		}
		
		final double[] genericStatScores = processGenericStats(result);

		final int[] conds = this.pickCondition(parallelData);
		final int picked_cond = conds[0];
		final int other_cond = conds[1];

		// sample
		final int sampleRuns = stat.getSampleRuns();
		if (sampleRuns > 0 ) {
			final double[] values = new double[sampleRuns];
			// TODO uncomment int stat_check = 0;
			final int[] genericStat2check = new int[genericStats.size()];
			Arrays.fill(genericStat2check, 0);
			final double[][] genericStatValues = new double [genericStats.size()][sampleRuns];
			
			for (int run = 0; run < sampleRuns; run++) {
				final ParallelData sampledData = sample(parallelData, picked_cond, other_cond);
				final Result sampledResult = stat.calculate(sampledData);
				final double sampledStat = sampledResult.getStat();
				values[run] = sampledStat;
				
				/* TODO uncomment
				if ((result.getStat() - sampledStat) > 0) {
					stat_check++;
				}*/

				for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
					final GenericStat genericStat = genericStats.get(genericStatI);
					
					final Result sampledGenericStatResult = genericStat.calculate(sampledData);
					final double sampledGenericStat = sampledGenericStatResult.getStat();
					
					genericStatValues[genericStatI][run] = sampledGenericStat;				
					if ((genericStatScores[genericStatI] - sampledGenericStat) > 0) {
						genericStat2check[genericStatI]++;
					}
				}
			}
			// write successful sampling
			result.getResultInfo().add("score_subsampled", Util.join(values, ','));
			/* TODO uncomment
			if (check >= limit) {
				result.getResultInfo().add("subsampling", "passed");
			} else {
				result.getResultInfo().add("subsampling", "failed");
			}*/
			

			// write successful sampling
			for (int genericStatI = 0; genericStatI < genericStats.size(); ++genericStatI) {
				final GenericStat genericStat = genericStats.get(genericStatI);
				final String scoreKey = genericStat.getScoreKey();

				final double[] sampledGenericValues = genericStatValues[genericStatI];
				result.getResultInfo().add(scoreKey + "_subsampled", Util.join(sampledGenericValues, ','));
				// TODO add checks
			}
		}
		
		return result;
	}
	
	// TODO reads or base call coverage
	// pick condition with highest number of reads
	public int[] pickCondition(final ParallelData parallelData) {
		int picked_cond = -1;
		int picked_cond_cov = -1;
		final int conditions = parallelData.getConditions();
	
		for (int condI = 0; condI < conditions - 1; condI++) {
			final int coverage = parallelData.getPooledData(condI).getPileupCount().getBaseCallCount();
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
		
		return new int [] {picked_cond, other_cond};
	}

	public ParallelData sample(final ParallelData parallelData, final int picked_cond, final int other_cond) {
		final DataContainer pooledData = parallelData.getPooledData(picked_cond).copy();
		final int[] targetCoverages = new int[parallelData.getData(other_cond).size()];
		for (int replicateI = 0; replicateI < parallelData.getData(other_cond).size(); replicateI++) {
			targetCoverages[replicateI] = parallelData.getData(other_cond).get(replicateI).getPileupCount().getBaseCallCount();
		}

		final ParallelData.Builder builder = new ParallelData.Builder(
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
		
		return sampledData;
	}
	
}
