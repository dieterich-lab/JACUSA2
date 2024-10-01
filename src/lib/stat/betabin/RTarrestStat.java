package lib.stat.betabin;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.arrest.AbstractRTarrestEstimationCountProvider;
import lib.util.Util;

public class RTarrestStat extends AbstractStat {

	public static final String ARREST_SCORE = "arrest_score";
	
	private final AbstractRTarrestEstimationCountProvider estContainerProv;
	private final RTarrestBetaBinParameter dirMultPrm;

	private final double threshold;
	private final EstimateDirMult dirMult;
	private final ChiSquaredDistribution dist;
	
	public RTarrestStat(
			final double threshold,
			final AbstractRTarrestEstimationCountProvider estContainerProv,
			final RTarrestBetaBinParameter dirMultPrm) {

		this.threshold			= threshold;
		this.estContainerProv 	= estContainerProv;
		this.dirMultPrm 		= dirMultPrm;
		
		dirMult	= new EstimateDirMult(dirMultPrm.getMinkaEstimateParameter());
		dist 	= new ChiSquaredDistribution(1);
	}

	@Override
	protected void processAfterCalculate(final Result statResult) {
		if (dirMultPrm.isShowAlpha()) {
			dirMult.addShowAlpha();
		}
		dirMult.addStatResultInfo(statResult.getResultInfo());
	}
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
		final double lrt 	= dirMult.getLRT(estContainers);
		final double pvalue = getPValue(lrt);
		final Result result = new OneStatResult(pvalue, parallelData);
		result.getResultInfo().add(ARREST_SCORE, Util.format(lrt));
		return result;
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(threshold)) {
			return false;
		}

		return threshold > statValue;
	}
	
}