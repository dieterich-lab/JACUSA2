package jacusa.method.lrtarrest;

import java.util.SortedMap;
import java.util.TreeMap;

import lib.data.ParallelData;
import lib.data.result.Result;
import lib.data.result.MultiStatResult;
import lib.stat.AbstractStat;

/**
 * TODO implement this test-statistic
 * 
 * @param 
 */
public class LRTarrestStat 
extends AbstractStat {

	// private final BetaBin betaBin;
	// private final DirMult dirMult;
	
	public LRTarrestStat(final LRTarrestStatFactory factory) {
		// betaBin = new BetaBin(new BetaBinFactory());
		// dirMult = new DirMult(factory, null, null); // TODO finish
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {
		// betaBin.calculate(statResult.getParellelData());
		// dirMult.calculate(statResult.getParellelData());
	}
	
	@Override
	public Result calculate(final ParallelData parallelData) {
		final int arrestPositions = 
				parallelData.getCombinedPooledData().getArrestPos2BaseCallCount().getPositions().size();
		final SortedMap<Integer, Double> statValues = new TreeMap<>();
		statValues.put(-1, Double.NaN);
		for (int valueIndex = 0; valueIndex < arrestPositions; ++valueIndex) {
			statValues.put(valueIndex, Double.NaN);
		}
		return new MultiStatResult(statValues, parallelData);
	}
	
	@Override
	protected boolean filter(final Result statResult) {
		return false; // TODO finish
	}
	
}
