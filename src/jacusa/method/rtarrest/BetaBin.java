package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.BetaBinParameter;

/**
 * TODO implement this test-statistic
 * 
 * @param 
 */
public class BetaBin 
extends AbstractStat {
	
	private final BetaBinParameter betaBinParameter;
	
	public BetaBin() {
		betaBinParameter = new BetaBinParameter();
	}

	@Override
	protected void addStatResultInfo(Result statResult) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Result calculate(final ParallelData parallelData) {
		// TODO Auto-generated method stub
		
		final double statValue = Double.NaN; 
		return new OneStatResult(statValue, parallelData);
	}
	
	@Override
	protected boolean filter(final Result statResult) {
		if (betaBinParameter.getThreshold() == Double.NaN) {
			return false;
		}
		return statResult.getStat(0) > betaBinParameter.getThreshold();
	}
	
}
