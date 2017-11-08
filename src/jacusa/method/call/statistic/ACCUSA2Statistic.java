package jacusa.method.call.statistic;

// TODO import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import jacusa.cli.parameters.StatisticParameters;
import jacusa.estimate.BayesEstimateParameters;
import lib.data.BaseCallConfig;
import lib.data.BaseQualData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.phred2prob.Phred2Prob;

/**
 * 
 * @author Michael Piechotta
 * 
 * Uses the matching coverage to calculate the test-statistic.
 * Tested if distributions are equal.
 * Same as in ACCUSA2 paper
 */
@Deprecated
public class ACCUSA2Statistic<T extends BaseQualData> 
implements StatisticCalculator<T> {

	private final StatisticParameters<T> parameters;
	private final BayesEstimateParameters estimateParameters;
	private final BaseCallConfig baseConfig;

	public ACCUSA2Statistic(final BaseCallConfig baseConfig, final StatisticParameters<T> parameters) {
		this.parameters = parameters;

		final int k = baseConfig.getBases().length;

		Phred2Prob phred2Prob = Phred2Prob.getInstance(k);
		estimateParameters = new BayesEstimateParameters(0.0, phred2Prob);
		this.baseConfig = baseConfig;
	}

	@Override
	public StatisticCalculator<T> newInstance() {
		return new ACCUSA2Statistic<T>(baseConfig, parameters);
	}

	@Override
	public void addStatistic(Result<T> result) {
		final double statistic = getStatistic(result.getParellelData());
		result.setStatistic(statistic);
	}
	
	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		/* TODO
		// use all bases for calculation
		final int baseIndexs[] = baseConfig.getBaseIndex();

		// first condition
		// probability matrix for all pileups in condition1 (bases in column, pileups in rows)
		final double[][] probs1 = estimateParameters.probabilityMatrix(baseIndexs, parallelData.getData(0));
		final DirichletDist dirichlet1 = getDirichlet(baseIndexs, parallelData.getData(0));
		final double density11 = getDensity(baseIndexs, probs1, dirichlet1);

		// second condition - see above
		final double[][] probs2 = estimateParameters.probabilityMatrix(baseIndexs, parallelData.getData(1));
		final DirichletDist dirichlet2 = getDirichlet(baseIndexs, parallelData.getData(1));
		final double density22 = getDensity(baseIndexs, probs2, dirichlet2);

		// null model - distributions are the same
		final double density12 = getDensity(baseIndexs, probs2, dirichlet1);
		final double density21 = getDensity(baseIndexs, probs1, dirichlet2);

		// calculate statistic z = log 0_Model - log A_Model 
		final double z = (density11 + density22) - (density12 + density21);
		 */
		int z = 0; // FIXME
		// use only positive numbers
		return Math.max(0, z);
		
	}

	/**
	 * Calculate the density for probs given dirichlet.
	 * @param dirichlet
	 * @param probs
	 * @return
	 */
	/*
	protected double getDensity(final int[] baseIs, final double[][] probs, final DirichletDist dirichlet) {
		double density = 0.0;

		// log10 prod = sum log10
		for(int i = 0; i < probs.length; ++i) {
			density += Math.log10(Math.max(Double.MIN_VALUE, dirichlet.density(probs[i])));
		}

		return density;
	}
	*/

	/* TODO
	protected DirichletDist getDirichlet(final int[] baseIndexs, final T[] pileupData) {
		final double[] alpha = estimateParameters.estimateAlpha(baseIndexs, pileupData);
		return new DirichletDist(alpha);
	}
	*/

	@Override
	public boolean filter(double value) {
		if (parameters.getThreshold() == Double.NaN) {
			return false;
		}
		
		return parameters.getThreshold() > value;
	}

	@Override
	public String getDescription() {
		return "ACCUSA2 statistic: Z=log10( Dir(alpha_A; phi_A) * Dir(alpha_B; phi_B) ) - log10( Dir(alpha_A; phi_B) * Dir(alpha_B; phi_A) )";
	}

	@Override
	public String getName() {
		return "ACCUSA2";
	}

	@Override
	public boolean processCLI(String line) {
		return false;
	}
	
}
