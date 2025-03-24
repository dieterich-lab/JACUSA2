package jacusa.worker;

import java.util.List;

import jacusa.method.pileup.PileupMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.INDELstat;
import lib.util.ExtendedInfo;
import lib.worker.AbstractWorker;

public class PileupWorker
extends AbstractWorker {
	
	private final AbstractStat stat;
	private final List<INDELstat> indelStats;
	
	public PileupWorker(final PileupMethod method, final int threadId, final AbstractStat stat, final List<INDELstat> indelStats) {
		super(method, threadId);
		this.stat 		= stat;
		this.indelStats = indelStats; 
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		final ExtendedInfo info = new ExtendedInfo();
		
		final Result result = stat.process(parallelData, null); 
		if (result == null) {
			return null;
		}
		
		for (final INDELstat indelStat : indelStats) {
			indelStat.process(parallelData, info);
		} 
		
		return result;
	}
	
}
