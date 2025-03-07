package lib.stat;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.INDELestimationCountProvider;
import lib.util.Info;
import lib.util.Util;

public class GenericStat extends AbstractStat {

	private final INDELestimationCountProvider estContainerProv;
	private final EstimateDirMult dirMult;
	private final ChiSquaredDistribution dist;
	
	private final String scoreKey;
	private final String pvalueKey;
	
	public GenericStat(
			final MinkaParameter minkaParameter,
			final INDELestimationCountProvider countSampleProvider,
			final String scoreKey,
			final String pvalueKey) {
		super();

		this.estContainerProv 	= countSampleProvider;
		this.dirMult			= new EstimateDirMult(minkaParameter);
		this.dist				= new ChiSquaredDistribution(1);
		
		this.scoreKey			= scoreKey;
		this.pvalueKey			= pvalueKey;
	}

	@Override
	protected boolean filter(Result statResult) {
		return false;
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
		final double lrt 	= dirMult.getLRT(estContainers);
		final Result result = new OneStatResult(lrt, parallelData);

		return result;
	}

	@Override
	protected void processAfterCalculate(final Result result) {
		final double lrt = result.getStat();
		final double pvalue = 1 - dist.cumulativeProbability(result.getStat());
	
		final Info resultInfo = result.getResultInfo();
		resultInfo.add(scoreKey, Util.format(lrt));
		resultInfo.add(pvalueKey, Util.format(pvalue));
	}
		
	public String getScoreKey() {
		return scoreKey;
	}
	
}
