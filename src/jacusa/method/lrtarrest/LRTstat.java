package jacusa.method.lrtarrest;

import jacusa.method.rtarrest.BetaBin;
import jacusa.method.rtarrest.BetaBinFactory;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.data.result.OneStatResult;
import lib.stat.AbstractStat;
import lib.stat.dirmult.DirMult;

/**
 * TODO implement this test-statistic
 * @author Michael Piechotta
 * @param 
 */
public class LRTstat 
extends AbstractStat {

	private final BetaBin betaBin;
	private final DirMult dirMult;
	
	public LRTstat(final LRTstatFactory factory) {
		super(factory);
		betaBin = new BetaBin(new BetaBinFactory());
		dirMult = new DirMult(factory, null, null); // TODO finish
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {
		betaBin.calculate(statResult.getParellelData());
		dirMult.calculate(statResult.getParellelData());
	}
	
	@Override
	public Result calculate(final ParallelData parallelData) {
		final double statValue = Double.NaN; 
		return new OneStatResult(statValue, parallelData);
	}
	
	@Override
	protected boolean filter(final Result statResult) {
		return false; // TODO finish
	}
	
}
