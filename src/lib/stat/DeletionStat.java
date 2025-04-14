package lib.stat;

import lib.estimate.MinkaParameter;
import lib.stat.estimation.provider.INDELestimateProvider;

public class DeletionStat extends INDELstat {

	public static final String SCORE = "deletion_score";
	public static final String PVALUE = "deletion_pvalue";
	
	public DeletionStat(
			final MinkaParameter minkaParameter,
			final INDELestimateProvider indelEstimateProvider) {
		super(minkaParameter, indelEstimateProvider, SCORE, PVALUE);
	}
	
}
