package jacusa.worker;

import java.util.List;

import jacusa.method.call.CallMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.stat.INDELstat;
import lib.stat.dirmult.CallStat;
import lib.stat.sampling.SubSampleStat;
import lib.util.ExtendedInfo;

import lib.worker.AbstractWorker;


/**
 * Method "call" specific worker.
 */
public class CallWorker extends AbstractWorker {

	private final CallStat callStat;
	private final List<INDELstat> indelStats;
	private final SubSampleStat subSampleStat;
	
	public CallWorker(
			final CallMethod method, 
			final int threadId, 
			final CallStat callStat, 
			final List<INDELstat> indelStats, 
			final SubSampleStat subSampleStat) {
		super(method, threadId);
		
		this.callStat 		= callStat;
		this.indelStats 	= indelStats;
		this.subSampleStat 	= subSampleStat;
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		final ExtendedInfo info = new ExtendedInfo(parallelData.getReplicates());
		final Result callResult = callStat.process(parallelData, info);
		
		if (callResult == null) {
			return null;
		}
		
		for (final INDELstat indelStat : indelStats) {
			indelStat.process(parallelData, info);
		}
		
		if (subSampleStat != null) {
			subSampleStat.subSample(callResult, callStat, indelStats);
		}
		
		return callResult;
	}

}
